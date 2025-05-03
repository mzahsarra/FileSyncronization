package fr.urouen.sync.sync;

/**
 * Observer that logs synchronization events to the console.
 */
public class SyncObserver implements Observer {
    @Override
    public void update(String message) {
        System.out.println("Changement détecté : " + message);
    }
}