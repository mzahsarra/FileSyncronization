package fr.urouen.sync;

import fr.urouen.sync.profile.ProfileBuilder;
import fr.urouen.sync.profile.ProfileManager;
import fr.urouen.sync.sync.SyncFacade;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // Nom du profil à tester
            String profileName = "profiletest";

            // Récupérer l'instance de ProfileManager
            ProfileManager pm = ProfileManager.getInstance();

            // Vérifier si le profil existe déjà
            if (pm.profileExists(profileName)) {
                // Supprimer le profil existant si nécessaire
                pm.deleteProfile(profileName);
                System.out.println("Ancien profil supprimé avec succès");
            }

            // Créer un nouveau profil avec le builder
            new ProfileBuilder()
                    .setProfileName(profileName)
                    .setPathA("D:/A")
                    .setPathB("D:/B")
                    .build();

            System.out.println("Nouveau profil créé avec succès");

            // Lancer la synchronisation avec la façade
            SyncFacade syncFacade = new SyncFacade();
            syncFacade.synchronize(profileName);
            System.out.println("Synchronisation terminée avec succès");

        } catch (IOException e) {
            System.err.println("Erreur d'entrée/sortie : " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Une erreur inattendue est survenue : " + e.getMessage());
        }
    }
}

