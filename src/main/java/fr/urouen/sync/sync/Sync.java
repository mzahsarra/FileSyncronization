package fr.urouen.sync.sync;

import fr.urouen.sync.profile.Profile;
import fr.urouen.sync.profile.ProfileManager;
import fr.urouen.sync.model.SyncRegistry;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class Sync {
    private final FileComparator comparator = new FileComparator();
    private final ConflictResolver resolver = new ConflictResolver();

    public void synchronize(String profileName) throws IOException, ClassNotFoundException {
        Profile profile = ProfileManager.getInstance().loadProfile(profileName);
        SyncRegistry registry = new SyncRegistry();
        registry.loadFromFile(new File(profileName + ".sync"));

        File dirA = new File(profile.getPathA());
        File dirB = new File(profile.getPathB());

        processDirectory(dirA, dirB, "", registry);

        ProfileManager.getInstance().saveProfileToFile(profile, registry);
    }

    private void processDirectory(File dirA, File dirB, String relativePath, SyncRegistry registry) throws IOException {
        File[] filesA = dirA.listFiles();
        File[] filesB = dirB.listFiles();

        Map<String, File> filesAMap = createFileMap(filesA);
        Map<String, File> filesBMap = createFileMap(filesB);

        for (String fileName : filesAMap.keySet()) {
            File fileA = filesAMap.get(fileName);
            File fileB = filesBMap.get(fileName);

            if (fileB == null) {
                copyFile(fileA, new File(dirB, fileA.getName()));
            } else {
                compareFiles(fileA, fileB, relativePath, registry);
            }
        }

        for (String fileName : filesBMap.keySet()) {
            if (!filesAMap.containsKey(fileName)) {
                File fileB = filesBMap.get(fileName);
                copyFile(fileB, new File(dirA, fileB.getName()));
            }
        }

        // Gestion des fichiers supprimés
        for (String fileName : filesAMap.keySet()) {
            if (!filesBMap.containsKey(fileName)) {
                File fileA = filesAMap.get(fileName);
                deleteFile(fileA, new File(dirB, fileA.getName()));
            }
        }

        for (String fileName : filesBMap.keySet()) {
            if (!filesAMap.containsKey(fileName)) {
                File fileB = filesBMap.get(fileName);
                deleteFile(fileB, new File(dirA, fileB.getName()));
            }
        }

        for (String fileName : filesAMap.keySet()) {
            File fileA = filesAMap.get(fileName);
            File fileB = filesBMap.get(fileName);

            if (fileA.isDirectory() && fileB != null && fileB.isDirectory()) {
                processDirectory(fileA, fileB, relativePath + "/" + fileName, registry);
            }
        }
    }

    private void compareFiles(File fileA, File fileB, String relativePath, SyncRegistry registry) throws IOException {
        int comparisonResult = comparator.compare(fileA, fileB, System.currentTimeMillis());

        if (comparisonResult == FileComparator.CONFLICT) {
            String resolution = resolver.resolve(fileA, fileB);
            registry.addConflictResolution(relativePath, resolution);

            Profile profile = ProfileManager.getInstance().loadProfile(relativePath);
            ProfileManager.getInstance().saveProfileToFile(profile, registry);
        } else if (comparisonResult == FileComparator.A_NEWER) {
            copyFile(fileA, fileB);
        } else if (comparisonResult == FileComparator.B_NEWER) {
            copyFile(fileB, fileA);
        }
    }

    private void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Fichier copié : " + source.getPath() + " -> " + dest.getPath());
    }

    private void deleteFile(File source, File dest) {
        if (dest.exists()) {
            if (dest.delete()) {
                System.out.println("Fichier supprimé : " + dest.getPath());
            } else {
                System.err.println("Erreur de suppression du fichier : " + dest.getPath());
            }
        }
    }

    private Map<String, File> createFileMap(File[] files) {
        Map<String, File> fileMap = new java.util.HashMap<>();
        for (File file : files) {
            if (file.isFile()) {
                fileMap.put(file.getName(), file);
            }
        }
        return fileMap;
    }
}
