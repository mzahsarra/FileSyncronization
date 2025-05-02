package fr.urouen.sync.profile;

import fr.urouen.sync.model.SyncRegistry;
import org.json.JSONObject;
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

    public synchronized void createProfile(String name, String pathA, String pathB) throws IOException {
        Objects.requireNonNull(name, "Profile name cannot be null");
        Objects.requireNonNull(pathA, "PathA cannot be null");
        Objects.requireNonNull(pathB, "PathB cannot be null");

        synchronized (profiles) {
            if (new File(name + ".sync").exists()) {
                throw new IllegalArgumentException("Le profil '" + name + "' existe déjà.");
            }

            Profile profile = new Profile(name, pathA, pathB);
            saveProfileToFile(profile, new SyncRegistry());
            profiles.put(name, profile);
        }
    }

    public void saveProfileToFile(Profile profile, SyncRegistry registry) throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONObject profileJson = new JSONObject();
        profileJson.put("name", profile.getName());
        profileJson.put("pathA", profile.getPathA());
        profileJson.put("pathB", profile.getPathB());
        profileJson.put("registry", new JSONObject(profile.getRegistry()));

        jsonObject.put("profile", profileJson);
        jsonObject.put("conflictResolutions", new JSONObject(registry.getConflictResolutions()));

        File file = new File(profile.getName() + ".sync");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonObject.toString(2));
        }
    }

    public Profile loadProfile(String name) throws IOException {
        synchronized (profiles) {
            if (profiles.containsKey(name)) {
                return profiles.get(name);
            }

            File file = new File(name + ".sync");
            if (!file.exists()) {
                throw new FileNotFoundException("Profile '" + name + "' not found");
            }

            try (FileReader reader = new FileReader(file)) {
                StringBuilder content = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    content.append((char) c);
                }

                JSONObject jsonObject = new JSONObject(content.toString());
                JSONObject profileJson = jsonObject.getJSONObject("profile");

                String profileName = profileJson.getString("name");
                String pathA = profileJson.getString("pathA");
                String pathB = profileJson.getString("pathB");
                Profile profile = new Profile(profileName, pathA, pathB);

                JSONObject registryJson = profileJson.getJSONObject("registry");
                for (String key : registryJson.keySet()) {
                    profile.updateEntry(key, registryJson.getLong(key));
                }

                profiles.put(name, profile);
                return profile;
            }
        }
    }

    public boolean profileExists(String name) {
        synchronized (profiles) {
            return profiles.containsKey(name) || new File(name + ".sync").exists();
        }
    }

    public void deleteProfile(String name) throws IOException {
        File profileFile = new File(name + ".sync");
        if (profileFile.exists()) {
            if (!profileFile.delete()) {
                throw new IOException("Impossible de supprimer le profil existant");
            }
        }
        profiles.remove(name);
    }
}
