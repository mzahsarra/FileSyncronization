package fr.urouen.sync.sync;

/**
 * Interface for observers of synchronization events, implementing the Observer pattern.
 */
public interface Observer {
    /**
     * Updates the observer with a message.
     * @param message the event message
     */
    void update(String message);
}