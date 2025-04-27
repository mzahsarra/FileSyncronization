package fr.urouen.sync.sync;

import java.io.File;

public class FileComparator {
    public static final int A_NEWER = 1;
    public static final int B_NEWER = 2;
    public static final int CONFLICT = 3;
    public static final int EQUAL = 0;

    // Compare deux fichiers en fonction de leur date de modification
    public int compare(File fileA, File fileB, long lastSync) {
        boolean aExists = fileA.exists();
        boolean bExists = fileB.exists();

        // Si les deux fichiers n'existent pas, ils sont considérés comme égaux
        if (!aExists && !bExists) return EQUAL;

        // Si A existe mais pas B, A est considéré comme plus récent
        if (aExists && !bExists) return A_NEWER;

        // Si B existe mais pas A, B est considéré comme plus récent
        if (!aExists && bExists) return B_NEWER;

        // Récupère les dates de modification des fichiers
        long aMod = fileA.lastModified();
        long bMod = fileB.lastModified();

        // Si les deux fichiers ont été modifiés après la dernière synchronisation, c'est un conflit
        if (aMod > lastSync && bMod > lastSync) return CONFLICT;

        // Comparaison des dates de modification
        if (aMod > bMod) return A_NEWER;
        if (bMod > aMod) return B_NEWER;

        // Si les dates sont identiques, les fichiers sont considérés égaux
        return EQUAL;
    }
}