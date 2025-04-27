package fr.urouen.sync.sync;

import fr.urouen.sync.profile.ProfileFactory;
import fr.urouen.sync.profile.ProfileManager;

import java.io.File;
import java.io.IOException;

public class SyncFacade {
    private final Sync sync;

    public SyncFacade() {
        this.sync = new Sync();
    }

    public void createProfile(String profileName, String pathA, String pathB) throws IOException {
        ProfileFactory.createProfile(profileName, pathA, pathB);
    }

    public void synchronize(String profileName) throws IOException, ClassNotFoundException {
        sync.synchronize(profileName);
    }

    public boolean profileExists(String profileName) {
        return new File(profileName + ".sync").exists();
    }

    public void deleteProfile(String profileName) throws IOException {
        ProfileManager.getInstance().deleteProfile(profileName);
    }
}