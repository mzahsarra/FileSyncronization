package fr.urouen.sync.profile;

import java.io.IOException;

public class ProfileFactory {
    public static void createProfile(String profileName, String pathA, String pathB) throws IOException {
        ProfileManager.getInstance().createProfile(profileName, pathA, pathB);
    }
}