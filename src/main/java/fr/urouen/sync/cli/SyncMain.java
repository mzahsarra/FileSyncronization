package fr.urouen.sync.cli;

import fr.urouen.sync.sync.Sync;

public class SyncMain {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: sync <nom-profil>");
            System.exit(1);
        }

        try {
            new Sync().synchronize(args[0]);
            System.out.println("Synchronisation terminée");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            System.exit(1);
        }
    }
}