package fr.urouen.sync.filesystem;

import java.io.IOException;
import java.util.List;

/**
 * Defines operations for accessing a file system, enabling extensibility to network protocols like WebDAV.
 */
public interface FileSystem {
    /**
     * Lists files and directories in the specified path.
     * @param path the directory path
     * @return a list of file information
     * @throws IOException if an I/O error occurs
     */
    List<FileInfo> listFiles(String path) throws IOException;

    /**
     * Gets the last modification time of a file.
     * @param path the file path
     * @return the modification time in milliseconds
     * @throws IOException if an I/O error occurs
     */
    long getLastModified(String path) throws IOException;

    /**
     * Copies a file from source to destination.
     * @param sourcePath the source file path
     * @param destPath the destination file path
     * @throws IOException if an I/O error occurs
     */
    void copyFile(String sourcePath, String destPath) throws IOException;

    /**
     * Deletes a file.
     * @param path the file path
     * @throws IOException if an I/O error occurs
     */
    void deleteFile(String path) throws IOException;

    /**
     * Checks if a file or directory exists.
     * @param path the path to check
     * @return true if the path exists, false otherwise
     * @throws IOException if an I/O error occurs
     */
    boolean exists(String path) throws IOException;

    /**
     * Checks if a path is a directory.
     * @param path the path to check
     * @return true if the path is a directory, false otherwise
     * @throws IOException if an I/O error occurs
     */
    boolean isDirectory(String path) throws IOException;
}

/**
 * Represents information about a file or directory.
 */
class FileInfo {
    private final String name;
    private final boolean isDirectory;

    public FileInfo(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}