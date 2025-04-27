package fr.urouen.sync.sync;

import fr.urouen.sync.model.SyncRegistry;
import fr.urouen.sync.profile.Profile;
import fr.urouen.sync.profile.ProfileManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SyncStat {

    // Affiche les informations du profil et du registre de synchronisation
    public void displayStatus(String profileName) throws IOException, ClassNotFoundException {
        // Charger le profil
        Profile profile = ProfileManager.getInstance().loadProfile(profileName);

        // Charger le registre de synchronisation
        SyncRegistry registry = new SyncRegistry();
        registry.loadFromFile(new File(profileName + ".sync"));

        // Afficher les informations du profil
        System.out.println("Profil: " + profile.getName());
        System.out.println("Chemin A: " + profile.getPathA());
        System.out.println("Chemin B: " + profile.getPathB());
        System.out.println("Dernière synchronisation:");

        // Afficher les résolutions de conflits
        if (registry.getConflictResolutions().isEmpty()) {
            System.out.println("Aucun conflit n'a été résolu.");
        } else {
            for (Map.Entry<String, String> entry : registry.getConflictResolutions().entrySet()) {
                System.out.println("Conflit: " + entry.getKey() + " -> " + entry.getValue());
            }
        }
    }
}