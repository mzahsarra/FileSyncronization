package fr.urouen.sync.profile;

import java.io.IOException;

public class ProfileBuilder {
    private String profileName;
    private String pathA;
    private String pathB;

    public ProfileBuilder setProfileName(String profileName) {
        this.profileName = profileName;
        return this;
    }

    public ProfileBuilder setPathA(String pathA) {
        this.pathA = pathA;
        return this;
    }

    public ProfileBuilder setPathB(String pathB) {
        this.pathB = pathB;
        return this;
    }

    // Crée et enregistre le profil
    public ProfileManager build() {
        try {
            ProfileManager profileManager = ProfileManager.getInstance();
            // Vérifie si le profil existe déjà, sinon crée le profil
            profileManager.createProfile(profileName, pathA, pathB);
            return profileManager;
        } catch (IOException e) {
            throw new RuntimeException("Échec de la création du profil: " + e.getMessage(), e);
        }
    }
}