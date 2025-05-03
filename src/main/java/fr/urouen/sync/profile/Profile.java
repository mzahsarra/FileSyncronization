package fr.urouen.sync.profile;

import java.util.Objects;

public final class Profile {
    private final String name;
    private final String pathA;
    private final String pathB;

    public Profile(String name, String pathA, String pathB) {
        this.name = Objects.requireNonNull(name, "Profile name cannot be null");
        this.pathA = Objects.requireNonNull(pathA, "PathA cannot be null");
        this.pathB = Objects.requireNonNull(pathB, "PathB cannot be null");
    }

    public String getName() { return name; }
    public String getPathA() { return pathA; }
    public String getPathB() { return pathB; }
}