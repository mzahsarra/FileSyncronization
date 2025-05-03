package fr.urouen.sync.ui;

import java.io.File;
import java.util.Scanner;

/**
 * Manages user interface interactions, including action display and conflict resolution.
 */
public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Displays an action message to the console.
     * @param message the message to display
     */
    public void displayAction(String message) {
        System.out.println(message);
    }

    /**
     * Resolves a conflict between two files by prompting the user.
     * @param fileA the file in directory A
     * @param fileB the file in directory B
     * @return "Version A" or "Version B" based on user choice or latest modification
     */
    public String resolveConflict(File fileA, File fileB) {
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
            return fileA.lastModified() > fileB.lastModified() ? "Version A" : "Version B";
        }
        return choice == 1 ? "Version A" : "Version B";
    }
}