package fr.urouen.sync.exception;

/**
 * Exception thrown when file access fails.
 */
public class FileAccessException extends SyncException {
    public FileAccessException(String message) {
        super(message);
    }

    public FileAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}