package fr.urouen.sync.model;

import org.json.JSONObject;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SyncRegistry {
    private final Map<String, String> conflictResolutions = new HashMap<>();

    public void saveToFile(File file) throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONObject resolutions = new JSONObject(conflictResolutions);
        jsonObject.put("conflictResolutions", resolutions);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonObject.toString(2));
        }
    }

    public void loadFromFile(File file) throws IOException {
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                StringBuilder content = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    content.append((char) c);
                }
                JSONObject jsonObject = new JSONObject(content.toString());
                JSONObject resolutions = jsonObject.getJSONObject("conflictResolutions");
                for (String key : resolutions.keySet()) {
                    conflictResolutions.put(key, resolutions.getString(key));
                }
            } catch (Exception e) {
                throw new IOException("Malformed JSON in registry file", e);
            }
        }
    }

    public void addConflictResolution(String relativePath, String resolution) {
        conflictResolutions.put(relativePath, resolution);
    }

    public Map<String, String> getConflictResolutions() {
        return new HashMap<>(conflictResolutions);
    }
}
