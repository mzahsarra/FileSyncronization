package fr.urouen.sync.model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SyncRegistry {
    private final Map<String, String> conflictResolutions = new HashMap<>();

    // Sauvegarde le registre de synchronisation dans un fichier
    public void saveToFile(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(conflictResolutions);
        }
    }

    // Charge le registre depuis un fichier
    public void loadFromFile(File file) throws IOException, ClassNotFoundException {
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof Map) {
                    conflictResolutions.putAll((Map<String, String>) obj);
                }
            }
        }
    }

    // Ajoute une résolution de conflit au registre
    public void addConflictResolution(String relativePath, String resolution) {
        conflictResolutions.put(relativePath, resolution);
    }

    // Récupère les résolutions de conflits
    public Map<String, String> getConflictResolutions() {
        return conflictResolutions;
    }
}