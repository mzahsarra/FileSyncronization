package fr.urouen.sync.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Profile {
    private final String name;
    private final String pathA;
    private final String pathB;
    private final Map<String, Long> registry = new HashMap<>();

    public Profile(String name, String pathA, String pathB) {
        this.name = Objects.requireNonNull(name, "Profile name cannot be null");
        this.pathA = Objects.requireNonNull(pathA, "PathA cannot be null");
        this.pathB = Objects.requireNonNull(pathB, "PathB cannot be null");
    }

    public String getName() {
        return name;
    }

    public String getPathA() {
        return pathA;
    }

    public String getPathB() {
        return pathB;
    }

    public Map<String, Long> getRegistry() {
        return new HashMap<>(registry);
    }

    public void updateEntry(String relativePath, long timestamp) {
        registry.put(relativePath, timestamp);
    }

    public Long getLastSync(String relativePath) {
        return registry.get(relativePath);
    }
}
