package fr.urouen.sync.sync;

import fr.urouen.sync.exception.SyncException;
import fr.urouen.sync.profile.ProfileManager;
import java.io.IOException;

/**
 * Provides a simplified interface to the synchronization subsystem, implementing the Facade pattern.
 */
public class SyncFacade {
    private final Sync sync;

    /**
     * Constructs a new SyncFacade.
     */
    public SyncFacade() {
        this.sync = new Sync();
    }

    /**
     * Synchronizes the directories specified in the given profile.
     * @param profileName the name of the profile to synchronize
     * @throws SyncException if an error occurs during synchronization
     */
    public void synchronize(String profileName) throws SyncException {
        sync.synchronize(profileName);
    }

    /**
     * Deletes the specified profile.
     * @param profileName the name of the profile to delete
     * @throws IOException if the profile cannot be deleted
     */
    public void deleteProfile(String profileName) throws IOException {
        ProfileManager.getInstance().deleteProfile(profileName);
    }
}