package fr.urouen.sync.sync;

import java.io.File;
import java.util.Scanner;

public class ConflictResolver {
    private Scanner scanner = new Scanner(System.in);

    // Résoudre un conflit entre deux fichiers
    public boolean resolve(File fileA, File fileB) {
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
            return fileA.lastModified() > fileB.lastModified();  // Conserver la version la plus récente
        }

        return choice == 1;  // Retourne true si l'utilisateur choisit la version A
    }
}
