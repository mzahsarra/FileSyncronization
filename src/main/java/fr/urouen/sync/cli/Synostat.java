package fr.urouen.sync.cli;

import fr.urouen.sync.profile.Profile;
import fr.urouen.sync.profile.ProfileManager;
import fr.urouen.sync.model.SyncRegistry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Synostat {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: synostat <nom-profil>");
            System.exit(1);
        }
        try {
            ProfileManager pm = ProfileManager.getInstance();
            Profile profile = pm.loadProfile(args[0]);
            SyncRegistry registry = new SyncRegistry();
            registry.loadFromFile(new File(args[0] + ".sync"));

            System.out.println("Profil: " + profile.getName());
            System.out.println("Dossier A: " + profile.getPathA());
            System.out.println("Dossier B: " + profile.getPathB());

            System.out.println("\nRegistre (dates de modification):");
            if (registry.getLastModified().isEmpty()) {
                System.out.println("Aucun fichier synchronisé.");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                registry.getLastModified().forEach((path, timestamp) ->
                        System.out.println(path + " -> " + sdf.format(new Date(timestamp)))
                );
            }

            System.out.println("\nRésolutions de conflits:");
            if (registry.getConflictResolutions().isEmpty()) {
                System.out.println("Aucun conflit résolu.");
            } else {
                registry.getConflictResolutions().forEach((path, resolution) ->
                        System.out.println(path + " -> " + resolution));
            }
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            System.exit(1);
        }
    }
}