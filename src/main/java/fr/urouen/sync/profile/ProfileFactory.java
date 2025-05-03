package fr.urouen.sync.profile;

import java.io.IOException;

/**
 * Abstract factory for creating profiles, implementing the Factory Method pattern.
 */
public abstract class ProfileFactory {
    /**
     * Creates a profile with the specified name and paths.
     * @param profileName the profile name
     * @param pathA the path to directory A
     * @param pathB the path to directory B
     * @throws IOException if an error occurs during profile creation
     */
    public abstract void createProfile(String profileName, String pathA, String pathB) throws IOException;

    /**
     * Default factory for creating local profiles.
     */
    public static class LocalProfileFactory extends ProfileFactory {
        @Override
        public void createProfile(String profileName, String pathA, String pathB) throws IOException {
            ProfileManager.getInstance().createProfile(profileName, pathA, pathB);
        }
    }
}