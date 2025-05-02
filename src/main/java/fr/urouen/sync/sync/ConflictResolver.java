package fr.urouen.sync.sync;

import java.io.File;
import java.util.Scanner;

public class ConflictResolver {

    private Scanner scanner = new Scanner(System.in);

    // Méthode de résolution des conflits
    public String resolve(File fileA, File fileB) {
        System.out.println("\nCONFLIT: " + fileA.getName());
        System.out.println("A: " + fileA.lastModified() + " (" + fileA.length() + " bytes)");
        System.out.println("B: " + fileB.lastModified() + " (" + fileB.length() + " bytes)");
        System.out.println("Choisir la version à conserver:");
        System.out.println("1. Version A");
        System.out.println("2. Version B");
        System.out.println("3. Conserver la version la plus récente");

        System.out.print("Votre choix: ");
        int choice = scanner.nextInt();

        if (choice == 3) {
            // Conserver la version la plus récente
            return fileA.lastModified() > fileB.lastModified() ? "Version A" : "Version B";
        }

        // Retourne la version choisie
        return choice == 1 ? "Version A" : "Version B";
    }
}
