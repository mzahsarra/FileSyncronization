package fr.urouen.sync.sync;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a file in the file system, implementing a simplified Composite pattern.
 * Only handles files, not directories.
 */
public class FileSystemElement {
    protected String name;
    protected long lastModified;
    protected String path;

    public static final int A_NEWER = 1;
    public static final int B_NEWER = 2;
    public static final int CONFLICT = 3;
    public static final int EQUAL = 0;

    /**
     * Constructs a FileSystemElement for a file.
     * @param name the file name
     * @param lastModified the last modification time
     * @param path the relative path (file name)
     */
    public FileSystemElement(String name, long lastModified, String path) {
        this.name = name;
        this.lastModified = lastModified;
        this.path = path;
    }

    /**
     * Gets the file's name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the file's path.
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Compares this file with another.
     * @param other the other file element
     * @param lastSync the last synchronization timestamp
     * @return comparison result (A_NEWER, B_NEWER, CONFLICT, or EQUAL)
     */
    public int compare(FileSystemElement other, long lastSync) {
        if (other == null) {
            return A_NEWER;
        }

        if (this.lastModified > lastSync && other.lastModified > lastSync) {
            return CONFLICT;
        }
        if (this.lastModified > other.lastModified || this.lastModified > lastSync) {
            return A_NEWER;
        }
        if (other.lastModified > this.lastModified || other.lastModified > lastSync) {
            return B_NEWER;
        }
        return EQUAL;
    }
}