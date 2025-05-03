package fr.urouen.sync.profile;

import java.io.IOException;

/**
 * Builds a Profile object step-by-step, implementing the Builder pattern.
 */
public class ProfileBuilder {
    private String profileName;
    private String pathA;
    private String pathB;

    /**
     * Sets the profile name.
     * @param profileName the profile name
     * @return this builder
     */
    public ProfileBuilder setProfileName(String profileName) {
        this.profileName = profileName;
        return this;
    }

    /**
     * Sets the path to directory A.
     * @param pathA the path to directory A
     * @return this builder
     */
    public ProfileBuilder setPathA(String pathA) {
        this.pathA = pathA;
        return this;
    }

    /**
     * Sets the path to directory B.
     * @param pathB the path to directory B
     * @return this builder
     */
    public ProfileBuilder setPathB(String pathB) {
        this.pathB = pathB;
        return this;
    }

    /**
     * Builds and saves the profile.
     * @return the created Profile
     * @throws IOException if profile creation fails
     */
    public Profile build() throws IOException {
        ProfileFactory factory = new ProfileFactory.LocalProfileFactory();
        factory.createProfile(profileName, pathA, pathB);
        return ProfileManager.getInstance().loadProfile(profileName);
    }
}