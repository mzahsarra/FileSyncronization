package fr.urouen.sync.profile;

import fr.urouen.sync.model.SyncRegistry;
import org.json.JSONObject;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileManager {
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
        if (profiles.containsKey(name) || new File(name + ".sync").exists()) {
            throw new IOException("Profile '" + name + "' already exists");
        }
        Profile profile = new Profile(name, pathA, pathB);
        profiles.put(name, profile);
        saveProfileToFile(profile, new SyncRegistry());
    }

    public synchronized void saveProfileToFile(Profile profile, SyncRegistry registry) throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONObject profileJson = new JSONObject();
        profileJson.put("name", profile.getName());
        profileJson.put("pathA", profile.getPathA());
        profileJson.put("pathB", profile.getPathB());
        jsonObject.put("profile", profileJson);
        jsonObject.put("lastModified", new JSONObject(registry.getLastModified()));
        jsonObject.put("conflictResolutions", new JSONObject(registry.getConflictResolutions()));
        File file = new File(profile.getName() + ".sync");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonObject.toString(2));
        }
    }

    public synchronized Profile loadProfile(String name) throws IOException {
        Objects.requireNonNull(name, "Profile name cannot be null");
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
            profiles.put(name, profile);
            return profile;
        } catch (Exception e) {
            throw new IOException("Failed to load profile: " + e.getMessage(), e);
        }
    }

    public synchronized boolean profileExists(String name) {
        return profiles.containsKey(name) || new File(name + ".sync").exists();
    }

    public synchronized void deleteProfile(String name) throws IOException {
        Objects.requireNonNull(name, "Profile name cannot be null");
        File file = new File(name + ".sync");
        if (!file.exists()) {
            throw new FileNotFoundException("Profile '" + name + "' not found");
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete profile '" + name + "'");
        }
        profiles.remove(name);
    }
}