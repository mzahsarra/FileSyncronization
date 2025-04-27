package fr.urouen.sync.sync;

import fr.urouen.sync.model.SyncRegistry;
import fr.urouen.sync.profile.Profile;
import fr.urouen.sync.profile.ProfileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class Sync {
    private final FileComparator comparator = new FileComparator();
    private final ConflictResolver resolver = new ConflictResolver();

    // Synchronisation de deux répertoires
    public void synchronize(String profileName) throws IOException, ClassNotFoundException {
        Profile profile = ProfileManager.getInstance().loadProfile(profileName);
        SyncRegistry registry = new SyncRegistry();
        registry.loadFromFile(new File(profileName + ".sync"));

        // Vérification des répertoires avant synchronisation
        File dirA = new File(profile.getPathA());
        File dirB = new File(profile.getPathB());

        // Vérification si les répertoires existent, sinon les créer
        if (!dirA.exists()) {
            System.out.println("Le répertoire A n'existe pas. Création du répertoire A...");
            dirA.mkdirs();  // Crée le répertoire A s'il n'existe pas
        }

        if (!dirB.exists()) {
            System.out.println("Le répertoire B n'existe pas. Création du répertoire B...");
            dirB.mkdirs();  // Crée le répertoire B s'il n'existe pas
        }

        // Procéder à la synchronisation après avoir vérifié ou créé les répertoires
        processDirectory(
                dirA,
                dirB,
                "",
                registry
        );

        registry.saveToFile(new File(profileName + ".sync"));
        System.out.println("Synchronisation terminée avec succès");
    }

    // Traite les répertoires en comparant les fichiers et en les synchronisant
    private void processDirectory(File dirA, File dirB, String relativePath, SyncRegistry registry) throws IOException {
        if (!dirA.exists() || !dirB.exists()) {
            System.out.println("L'un des répertoires n'existe pas.");
            return;
        }

        File[] filesA = dirA.listFiles();
        File[] filesB = dirB.listFiles();

        Map<String, File> filesAMap = createFileMap(filesA);
        Map<String, File> filesBMap = createFileMap(filesB);

        // Comparer les fichiers entre les deux répertoires
        for (String fileName : filesAMap.keySet()) {
            File fileA = filesAMap.get(fileName);
            File fileB = filesBMap.get(fileName);

            if (fileB == null) {
                copyFile(fileA, new File(dirB, fileA.getName()));
            } else {
                compareFiles(fileA, fileB, relativePath, registry);
            }
        }

        // Copier les fichiers présents seulement dans B
        for (String fileName : filesBMap.keySet()) {
            if (!filesAMap.containsKey(fileName)) {
                File fileB = filesBMap.get(fileName);
                copyFile(fileB, new File(dirA, fileB.getName()));
            }
        }

        // Traiter récursivement les sous-répertoires
        for (String fileName : filesAMap.keySet()) {
            File fileA = filesAMap.get(fileName);
            File fileB = filesBMap.get(fileName);

            if (fileA.isDirectory() && fileB != null && fileB.isDirectory()) {
                processDirectory(fileA, fileB, relativePath + "/" + fileName, registry);
            }
        }
    }

    // Compare deux fichiers et résout les conflits si nécessaire
    private void compareFiles(File fileA, File fileB, String relativePath, SyncRegistry registry) throws IOException {
        int comparisonResult = comparator.compare(fileA, fileB, System.currentTimeMillis());

        if (comparisonResult == FileComparator.EQUAL) {
            return;  // Les fichiers sont égaux
        }

        if (comparisonResult == FileComparator.A_NEWER) {
            copyFile(fileA, fileB);
        } else if (comparisonResult == FileComparator.B_NEWER) {
            copyFile(fileB, fileA);
        } else if (comparisonResult == FileComparator.CONFLICT) {
            if (resolver.resolve(fileA, fileB)) {
                copyFile(fileA, fileB);  // Conflit résolu en gardant la version A
            } else {
                copyFile(fileB, fileA);  // Conflit résolu en gardant la version B
            }
        }
    }

    // Créer une carte des fichiers à partir d'un tableau de fichiers
    private Map<String, File> createFileMap(File[] files) {
        Map<String, File> fileMap = new java.util.HashMap<>();
        for (File file : files) {
            if (file.isFile()) {
                fileMap.put(file.getName(), file);
            }
        }
        return fileMap;
    }

    // Copier un fichier d'un répertoire à un autre
    private void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Fichier copié : " + source.getPath() + " -> " + dest.getPath());
    }
}