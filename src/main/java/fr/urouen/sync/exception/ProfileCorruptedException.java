package fr.urouen.sync.exception;

/**
 * Exception thrown when a profile file is corrupted or malformed.
 */
public class ProfileCorruptedException extends SyncException {
    public ProfileCorruptedException(String message) {
        super(message);
    }

    public ProfileCorruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}