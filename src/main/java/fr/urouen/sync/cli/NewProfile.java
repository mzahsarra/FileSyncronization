package fr.urouen.sync.cli;

import fr.urouen.sync.profile.ProfileBuilder;

/**
 * Command-line program to create a new synchronization profile.
 */
public class NewProfile {
    /**
     * Creates a new profile with the specified name and directory paths.
     * @param args command-line arguments: <name> <pathA> <pathB>
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: new-profile <nom> <cheminA> <cheminB>");
            System.exit(1);
        }

        try {
            new ProfileBuilder()
                    .setProfileName(args[0])
                    .setPathA(args[1])
                    .setPathB(args[2])
                    .build();
            System.out.println("Profil créé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            System.exit(1);
        }
    }
}