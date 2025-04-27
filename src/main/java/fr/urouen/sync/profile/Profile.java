package fr.urouen.sync.profile;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Profile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String pathA;
    private final String pathB;
    private final Map<String, Long> registry = new HashMap<>();

    public Profile(String name, String pathA, String pathB) {
        this.name = Objects.requireNonNull(name);
        this.pathA = Objects.requireNonNull(pathA);
        this.pathB = Objects.requireNonNull(pathB);
    }

    // Getters
    public String getName() { return name; }
    public String getPathA() { return pathA; }
    public String getPathB() { return pathB; }
    public Map<String, Long> getRegistry() { return new HashMap<>(registry); }

    // Méthodes pour le registre
    public void updateEntry(String relativePath, long timestamp) {
        registry.put(relativePath, timestamp);
    }

    public Long getLastSync(String relativePath) {
        return registry.get(relativePath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return name.equals(profile.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}