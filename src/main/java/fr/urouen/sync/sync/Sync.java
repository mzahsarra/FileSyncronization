package fr.urouen.sync.sync;

import fr.urouen.sync.profile.Profile;
import fr.urouen.sync.profile.ProfileManager;
import fr.urouen.sync.model.SyncRegistry;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class Sync {
    private final FileComparator comparator = new FileComparator();
    private final ConflictResolver resolver = new ConflictResolver();

    public void synchronize(String profileName) throws IOException {
        Profile profile = ProfileManager.getInstance().loadProfile(profileName);
        SyncRegistry registry = new SyncRegistry();
        registry.loadFromFile(new File(profileName + ".sync"));

        File dirA = new File(profile.getPathA());
        File dirB = new File(profile.getPathB());
        if (!dirA.exists() || !dirA.isDirectory() || !dirB.exists() || !dirB.isDirectory()) {
            throw new IOException("Les dossiers A et B doivent exister et être accessibles");
        }

        processDirectory(dirA, dirB, "", registry);

        ProfileManager.getInstance().saveProfileToFile(profile, registry);
    }

    private void processDirectory(File dirA, File dirB, String relativePath, SyncRegistry registry) throws IOException {
        File[] filesA = dirA.listFiles();
        File[] filesB = dirB.listFiles();

        Map<String, File> filesAMap = createFileMap(filesA);
        Map<String, File> filesBMap = createFileMap(filesB);

        // Gestion des fichiers dans A
        for (String name : filesAMap.keySet()) {
            File fileA = filesAMap.get(name);
            File fileB = new File(dirB, name);
            String path = relativePath.isEmpty() ? name : relativePath + "/" + name;
            if (!filesBMap.containsKey(name)) {
                Long lastSync = registry.getLastSync(path);
                long aMod = fileA.lastModified();
                System.out.println("Fichier dans A, absent dans B : " + path + ", lastSync=" + lastSync + ", aMod=" + aMod);
                if (lastSync != null) {
                    // Fichier supprimé dans B, propager la suppression dans A
                    System.out.println("Suppression de " + fileA.getPath() + " (fichier supprimé dans B)");
                    deleteFile(fileA, fileB);
                    registry.removeEntry(path);
                } else {
                    // Fichier nouveau dans A, copier vers B
                    System.out.println("Copie de A vers B : " + path + " (cas + | ⊥)");
                    copyFile(fileA, fileB);
                    registry.addLastModified(path, aMod);
                }
            } else {
                compareFiles(fileA, fileB, path, registry);
            }
        }

        // Gestion des fichiers dans B
        for (String name : filesBMap.keySet()) {
            if (!filesAMap.containsKey(name)) {
                File fileB = filesBMap.get(name);
                File fileA = new File(dirA, name);
                String path = relativePath.isEmpty() ? name : relativePath + "/" + name;
                Long lastSync = registry.getLastSync(path);
                long bMod = fileB.lastModified();
                System.out.println("Fichier dans B, absent dans A : " + path + ", lastSync=" + lastSync + ", bMod=" + bMod);
                if (lastSync != null) {
                    // Fichier supprimé dans A, propager la suppression dans B
                    System.out.println("Suppression de " + fileB.getPath() + " (fichier supprimé dans A)");
                    deleteFile(fileB, fileA);
                    registry.removeEntry(path);
                } else {
                    // Fichier nouveau dans B, copier vers A
                    System.out.println("Copie de B vers A : " + path + " (cas ⊥ | +)");
                    copyFile(fileB, fileA);
                    registry.addLastModified(path, bMod);
                }
            }
        }

        // Parcours récursif des dossiers
        for (File fileA : filesA != null ? filesA : new File[0]) {
            if (fileA.isDirectory()) {
                File fileB = new File(dirB, fileA.getName());
                if (fileB.exists() && fileB.isDirectory()) {
                    processDirectory(fileA, fileB, relativePath.isEmpty() ? fileA.getName() : relativePath + "/" + fileA.getName(), registry);
                }
            }
        }
        for (File fileB : filesB != null ? filesB : new File[0]) {
            if (fileB.isDirectory()) {
                File fileA = new File(dirA, fileB.getName());
                if (fileA.exists() && fileA.isDirectory()) {
                    processDirectory(fileA, fileB, relativePath.isEmpty() ? fileB.getName() : relativePath + "/" + fileB.getName(), registry);
                }
            }
        }
    }

    private void compareFiles(File fileA, File fileB, String relativePath, SyncRegistry registry) throws IOException {
        Long lastSync = registry.getLastSync(relativePath);
        int comparisonResult = comparator.compare(fileA, fileB, lastSync != null ? lastSync : 0);
        System.out.println("Comparaison " + relativePath + ": result=" + comparisonResult + ", lastSync=" + lastSync);

        if (comparisonResult == FileComparator.CONFLICT) {
            String resolution = resolver.resolve(fileA, fileB);
            System.out.println("Conflit résolu pour " + relativePath + ": " + resolution);
            registry.addConflictResolution(relativePath, resolution);
            if (resolution.equals("Version A")) {
                copyFile(fileA, fileB);
            } else {
                copyFile(fileB, fileA);
            }
        } else if (comparisonResult == FileComparator.A_NEWER) {
            System.out.println("Copie de A vers B : " + relativePath);
            copyFile(fileA, fileB);
        } else if (comparisonResult == FileComparator.B_NEWER) {
            System.out.println("Copie de B vers A : " + relativePath);
            copyFile(fileB, fileA);
        }

        if (fileA.exists() && fileB.exists()) {
            long newTimestamp = Math.max(fileA.lastModified(), fileB.lastModified());
            System.out.println("Mise à jour registre pour " + relativePath + ": timestamp=" + newTimestamp);
            registry.addLastModified(relativePath, newTimestamp);
        }
    }

    private void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Fichier copié : " + source.getPath() + " -> " + dest.getPath());
    }

    private void deleteFile(File source, File dest) {
        if (source.exists()) { // Supprimer le fichier source (présent)
            if (source.delete()) {
                System.out.println("Fichier supprimé : " + source.getPath());
            } else {
                System.err.println("Erreur de suppression du fichier : " + source.getPath());
            }
        }
    }

    private Map<String, File> createFileMap(File[] files) {
        Map<String, File> fileMap = new HashMap<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileMap.put(file.getName(), file);
                    System.out.println("Ajouté au fileMap : " + file.getName());
                }
            }
        }
        return fileMap;
    }
}