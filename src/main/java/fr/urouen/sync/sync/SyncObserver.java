package fr.urouen.sync.sync;

class SyncObserver implements Observer {
    @Override
    public void update(String message) {
        System.out.println("Changement détecté : " + message);
    }
}
