package fr.urouen.sync.cli;

import fr.urouen.sync.profile.Profile;
import fr.urouen.sync.profile.ProfileManager;

public class Synostat {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: synostat <nom-profil>");
            System.exit(1);
        }

        try {
            Profile profile = ProfileManager.getInstance().loadProfile(args[0]);
            System.out.println("Profil: " + profile.getName());
            System.out.println("Dossier A: " + profile.getPathA());
            System.out.println("Dossier B: " + profile.getPathB());
            System.out.println("\nRegistre:");
            profile.getRegistry().forEach((k,v) ->
                    System.out.printf("%s -> %tF %<tT%n", k, v));
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            System.exit(1);
        }
    }
}