package fr.urouen.sync.model;

import org.json.JSONObject;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SyncRegistry {
    private final Map<String, Long> lastModified = new HashMap<>();
    private final Map<String, String> conflictResolutions = new HashMap<>();

    public void loadFromFile(File file) throws IOException {
        lastModified.clear();
        conflictResolutions.clear();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                StringBuilder content = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    content.append((char) c);
                }
                JSONObject jsonObject = new JSONObject(content.toString());
                JSONObject lastMod = jsonObject.optJSONObject("lastModified");
                if (lastMod != null) {
                    for (String key : lastMod.keySet()) {
                        lastModified.put(key, lastMod.getLong(key));
                    }
                }
                JSONObject resolutions = jsonObject.optJSONObject("conflictResolutions");
                if (resolutions != null) {
                    for (String key : resolutions.keySet()) {
                        conflictResolutions.put(key, resolutions.getString(key));
                    }
                }
            } catch (Exception e) {
                throw new IOException("Malformed JSON in registry file", e);
            }
        }
    }

    public void addLastModified(String relativePath, long timestamp) {
        lastModified.put(relativePath, timestamp);
    }

    public Long getLastSync(String relativePath) {
        return lastModified.get(relativePath);
    }

    public void removeEntry(String relativePath) {
        lastModified.remove(relativePath);
        conflictResolutions.remove(relativePath);
    }

    public void addConflictResolution(String relativePath, String resolution) {
        conflictResolutions.put(relativePath, resolution);
    }

    public Map<String, String> getConflictResolutions() {
        return new HashMap<>(conflictResolutions);
    }

    public Map<String, Long> getLastModified() {
        return new HashMap<>(lastModified);
    }
}