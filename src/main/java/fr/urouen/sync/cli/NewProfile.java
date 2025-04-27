package fr.urouen.sync.cli;


import fr.urouen.sync.profile.ProfileManager;

public class NewProfile {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: new-profile <nom> <cheminA> <cheminB>");
            System.exit(1);
        }

        try {
            ProfileManager.getInstance()
                    .createProfile(args[0], args[1], args[2]);
            System.out.println("Profil créé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            System.exit(1);
        }
    }
}
