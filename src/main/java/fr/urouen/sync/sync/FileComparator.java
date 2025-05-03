package fr.urouen.sync.sync;

import java.io.File;

public class FileComparator {
    public static final int A_NEWER = 1;
    public static final int B_NEWER = 2;
    public static final int CONFLICT = 3;
    public static final int EQUAL = 0;

    public int compare(File fileA, File fileB, long lastSync) {
        boolean aExists = fileA.exists();
        boolean bExists = fileB.exists();

        if (!aExists && !bExists) {
            return EQUAL;
        }
        if (aExists && !bExists) {
            return A_NEWER;
        }
        if (!aExists && bExists) {
            return B_NEWER;
        }

        long aMod = fileA.lastModified();
        long bMod = fileB.lastModified();

        if (aMod > lastSync && bMod > lastSync) {
            return CONFLICT;
        }
        if (aMod > bMod || aMod > lastSync) {
            return A_NEWER;
        }
        if (bMod > aMod || bMod > lastSync) {
            return B_NEWER;
        }
        return EQUAL;
    }
}