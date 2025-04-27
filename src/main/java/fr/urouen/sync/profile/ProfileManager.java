package fr.urouen.sync.profile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ProfileManager {
    private static final ProfileManager INSTANCE = new ProfileManager();
    private final Map<String, Profile> profiles = new HashMap<>();

    private ProfileManager() {}

    public static ProfileManager getInstance() {
        return INSTANCE;
    }

    // Crée un profil si le profil n'existe pas déjà
    public synchronized void createProfile(String name, String pathA, String pathB) throws IOException {
        Objects.requireNonNull(name, "Profile name cannot be null");
        Objects.requireNonNull(pathA, "PathA cannot be null");
        Objects.requireNonNull(pathB, "PathB cannot be null");

        synchronized (profiles) {
            // Vérifie si le profil existe déjà dans le système de fichiers
            if (new File(name + ".sync").exists()) {
                throw new IllegalArgumentException("Le profil '" + name + "' existe déjà. Utilisez un nom différent.");
            }

            Profile profile = new Profile(name, pathA, pathB);
            saveProfileToFile(profile);
            profiles.put(name, profile);
        }
    }

    // Sauvegarde un profil dans un fichier
    private void saveProfileToFile(Profile profile) throws IOException {
        File file = new File(profile.getName() + ".sync");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(profile);
        }
    }

    // Charge un profil depuis un fichier
    public Profile loadProfile(String name) throws IOException, ClassNotFoundException {
        synchronized (profiles) {
            // Vérifie d'abord en mémoire
            if (profiles.containsKey(name)) {
                return profiles.get(name);
            }

            // Charge depuis le fichier
            File file = new File(name + ".sync");
            if (!file.exists()) {
                throw new FileNotFoundException("Profile '" + name + "' not found");
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Profile profile = (Profile) ois.readObject();
                profiles.put(name, profile);
                return profile;
            }
        }
    }

    // Vérifie si le profil existe déjà
    public boolean profileExists(String name) {
        synchronized (profiles) {
            return profiles.containsKey(name) || new File(name + ".sync").exists();
        }
    }

    // Supprime un profil existant
    public synchronized void deleteProfile(String name) throws IOException {
        File profileFile = new File(name + ".sync");
        if (profileFile.exists()) {
            if (!profileFile.delete()) {
                throw new IOException("Impossible de supprimer le profil existant");
            }
        }
        profiles.remove(name);
    }
}