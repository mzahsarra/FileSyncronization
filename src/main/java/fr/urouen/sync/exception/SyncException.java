package fr.urouen.sync.exception;

import java.io.IOException;

/**
 * Base exception for synchronization-related errors.
 */
public class SyncException extends IOException {
    public SyncException(String message) {
        super(message);
    }

    public SyncException(String message, Throwable cause) {
        super(message, cause);
    }
}