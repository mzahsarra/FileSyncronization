package fr.urouen.sync.sync;

import fr.urouen.sync.exception.FileAccessException;
import fr.urouen.sync.exception.SyncException;
import fr.urouen.sync.profile.Profile;
import fr.urouen.sync.profile.ProfileManager;
import fr.urouen.sync.model.SyncRegistry;
import fr.urouen.sync.ui.ConsoleUI;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Synchronizes files (not directories) in two directories based on a profile, ensuring their contents are identical.
 * Uses Composite pattern for file representation and Observer pattern for action notifications.
 */
public class Sync {
    private final ConsoleUI consoleUI = new ConsoleUI();
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Constructs a Sync instance and registers a default observer.
     */
    public Sync() {
        observers.add(new SyncObserver());
    }

    /**
     * Registers an observer for synchronization events.
     * @param observer the observer to register
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Notifies all observers of an event.
     * @param message the event message
     */
    private void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    /**
     * Synchronizes the files in the directories specified in the given profile.
     * Only processes files directly in the root directories, ignoring subdirectories.
     * @param profileName the name of the profile to load
     * @throws SyncException if an error occurs during synchronization
     */
    public void synchronize(String profileName) throws SyncException {
        try {
            Profile profile = ProfileManager.getInstance().loadProfile(profileName);
            SyncRegistry registry = new SyncRegistry();
            registry.loadFromFile(new File(profileName + ".sync"));

            File dirA = new File(profile.getPathA());
            File dirB = new File(profile.getPathB());
            if (!dirA.exists() || !dirA.isDirectory() || !dirB.exists() || !dirB.isDirectory()) {
                throw new FileAccessException("Les dossiers A et B doivent exister et être accessibles");
            }

            List<FileSystemElement> filesA = buildFileList(dirA);
            List<FileSystemElement> filesB = buildFileList(dirB);
            processFiles(filesA, filesB, dirA, dirB, registry);

            ProfileManager.getInstance().saveProfileToFile(profile, registry);
        } catch (IOException e) {
            throw new SyncException("Failed to synchronize profile: " + profileName, e);
        }
    }

    /**
     * Builds a list of FileSystemElement for files (not directories) in the given directory.
     * @param dir the directory to scan
     * @return a list of file elements
     */
    private List<FileSystemElement> buildFileList(File dir) {
        List<FileSystemElement> files = new ArrayList<>();
        File[] fileArray = dir.listFiles(File::isFile);
        if (fileArray != null) {
            for (File file : fileArray) {
                String path = file.getName();
                notifyObservers("Building file element: " + path);
                files.add(new FileSystemElement(file.getName(), file.lastModified(), path));
            }
        }
        notifyObservers("Building file list for: " + dir.getPath());
        return files;
    }

    /**
     * Processes the lists of files from both directories, synchronizing them.
     * @param filesA list of files in directory A
     * @param filesB list of files in directory B
     * @param dirA directory A
     * @param dirB directory B
     * @param registry the synchronization registry
     * @throws IOException if an I/O error occurs
     */
    private void processFiles(List<FileSystemElement> filesA, List<FileSystemElement> filesB, File dirA, File dirB, SyncRegistry registry) throws IOException {
        for (FileSystemElement elemA : filesA) {
            FileSystemElement elemB = findElement(filesB, elemA.getName());
            if (elemB == null) {
                handleMissingB(elemA, new File(dirA, elemA.getName()), new File(dirB, elemA.getName()), elemA.getName(), registry);
            } else {
                compareFiles(elemA, elemB, new File(dirA, elemA.getName()), new File(dirB, elemB.getName()), elemA.getName(), registry);
            }
        }

        for (FileSystemElement elemB : filesB) {
            if (!containsElement(filesA, elemB.getName())) {
                handleMissingA(elemB, new File(dirB, elemB.getName()), new File(dirA, elemB.getName()), elemB.getName(), registry);
            }
        }
    }

    /**
     * Compares two file elements and synchronizes them if necessary.
     * @param elemA file element from directory A
     * @param elemB file element from directory B
     * @param fileA file in directory A
     * @param fileB file in directory B
     * @param relativePath the relative path (file name)
     * @param registry the synchronization registry
     * @throws IOException if an I/O error occurs
     */
    private void compareFiles(FileSystemElement elemA, FileSystemElement elemB, File fileA, File fileB, String relativePath, SyncRegistry registry) throws IOException {
        Long lastSync = registry.getLastSync(relativePath);
        long lastSyncTime = lastSync != null ? lastSync : 0;
        int comparisonResult = elemA.compare(elemB, lastSyncTime);
        consoleUI.displayAction("Comparaison " + relativePath + ": result=" + comparisonResult + ", lastSync=" + lastSync);
        notifyObservers("Compared " + relativePath + ": result=" + comparisonResult);

        if (comparisonResult == FileSystemElement.CONFLICT) {
            String resolution = consoleUI.resolveConflict(fileA, fileB);
            consoleUI.displayAction("Conflit résolu pour " + relativePath + ": " + resolution);
            notifyObservers("Conflict resolved for " + relativePath + ": " + resolution);
            registry.addConflictResolution(relativePath, resolution);
            if (resolution.equals("Version A")) {
                copyFile(fileA, fileB);
            } else {
                copyFile(fileB, fileA);
            }
        } else if (comparisonResult == FileSystemElement.A_NEWER) {
            consoleUI.displayAction("Copie de A vers B : " + relativePath);
            notifyObservers("Copying A to B: " + relativePath);
            copyFile(fileA, fileB);
        } else if (comparisonResult == FileSystemElement.B_NEWER) {
            consoleUI.displayAction("Copie de B vers A : " + relativePath);
            notifyObservers("Copying B to A: " + relativePath);
            copyFile(fileB, fileA);
        }

        if (fileA.exists() && fileB.exists()) {
            long newTimestamp = Math.max(fileA.lastModified(), fileB.lastModified());
            consoleUI.displayAction("Mise à jour registre pour " + relativePath + ": timestamp=" + newTimestamp);
            notifyObservers("Updated registry for " + relativePath + ": timestamp=" + newTimestamp);
            registry.addLastModified(relativePath, newTimestamp);
        }
    }

    /**
     * Handles a file present in A but missing in B.
     * @param elemA file element from directory A
     * @param fileA file in directory A
     * @param fileB file in directory B
     * @param relativePath the relative path (file name)
     * @param registry the synchronization registry
     * @throws IOException if an I/O error occurs
     */
    private void handleMissingB(FileSystemElement elemA, File fileA, File fileB, String relativePath, SyncRegistry registry) throws IOException {
        Long lastSync = registry.getLastSync(relativePath);
        long aMod = fileA.lastModified();
        consoleUI.displayAction("Fichier dans A, absent dans B : " + relativePath + ", lastSync=" + lastSync + ", aMod=" + aMod);
        notifyObservers("File in A, missing in B: " + relativePath);
        if (lastSync != null) {
            consoleUI.displayAction("Suppression de " + fileA.getPath() + " (fichier supprimé dans B)");
            notifyObservers("Deleting " + fileA.getPath() + " (file deleted in B)");
            deleteFile(fileA, fileB);
            registry.removeEntry(relativePath);
        } else {
            consoleUI.displayAction("Copie de A vers B : " + relativePath + " (cas + | ⊥)");
            notifyObservers("Copying A to B: " + relativePath + " (case + | ⊥)");
            copyFile(fileA, fileB);
            registry.addLastModified(relativePath, aMod);
        }
    }

    /**
     * Handles a file present in B but missing in A.
     * @param elemB file element from directory B
     * @param fileB file in directory B
     * @param fileA file in directory A
     * @param relativePath the relative path (file name)
     * @param registry the synchronization registry
     * @throws IOException if an I/O error occurs
     */
    private void handleMissingA(FileSystemElement elemB, File fileB, File fileA, String relativePath, SyncRegistry registry) throws IOException {
        Long lastSync = registry.getLastSync(relativePath);
        long bMod = fileB.lastModified();
        consoleUI.displayAction("Fichier dans B, absent dans A : " + relativePath + ", lastSync=" + lastSync + ", bMod=" + bMod);
        notifyObservers("File in B, missing in A: " + relativePath);
        if (lastSync != null) {
            consoleUI.displayAction("Suppression de " + fileB.getPath() + " (fichier supprimé dans A)");
            notifyObservers("Deleting " + fileB.getPath() + " (file deleted in A)");
            deleteFile(fileB, fileA);
            registry.removeEntry(relativePath);
        } else {
            consoleUI.displayAction("Copie de B vers A : " + relativePath + " (cas ⊥ | +)");
            notifyObservers("Copying B to A: " + relativePath + " (case ⊥ | +)");
            copyFile(fileB, fileA);
            registry.addLastModified(relativePath, bMod);
        }
    }

    /**
     * Finds a file element in a list by name.
     * @param elements list of file elements
     * @param name the name to find
     * @return the matching element, or null if not found
     */
    private FileSystemElement findElement(List<FileSystemElement> elements, String name) {
        for (FileSystemElement elem : elements) {
            if (elem.getName().equals(name)) {
                return elem;
            }
        }
        return null;
    }

    /**
     * Checks if a file element exists in a list by name.
     * @param elements list of file elements
     * @param name the name to check
     * @return true if the name exists, false otherwise
     */
    private boolean containsElement(List<FileSystemElement> elements, String name) {
        return findElement(elements, name) != null;
    }

    /**
     * Copies a file from source to destination.
     * @param source the source file
     * @param dest the destination file
     * @throws IOException if an I/O error occurs
     */
    private void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        consoleUI.displayAction("Fichier copié : " + source.getPath() + " -> " + dest.getPath());
        notifyObservers("File copied: " + source.getPath() + " -> " + dest.getPath());
    }

    /**
     * Deletes a file.
     * @param source the file to delete
     * @param dest the corresponding file in the other directory
     * @throws IOException if an I/O error occurs
     */
    private void deleteFile(File source, File dest) throws FileAccessException {
        if (source.exists()) {
            try {
                if (source.delete()) {
                    consoleUI.displayAction("Fichier supprimé : " + source.getPath());
                    notifyObservers("File deleted: " + source.getPath());
                } else {
                    consoleUI.displayAction("Erreur de suppression du fichier : " + source.getPath());
                    notifyObservers("Error deleting file: " + source.getPath());
                }
            } catch (SecurityException e) {
                throw new FileAccessException("Failed to delete file: " + source.getPath(), e);
            }
        }
    }
}