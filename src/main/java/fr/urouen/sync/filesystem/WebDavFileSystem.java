package fr.urouen.sync.filesystem;

import java.io.IOException;
import java.util.List;

/**
 * Stub implementation of FileSystem for WebDAV, demonstrating network extensibility.
 */
public class WebDavFileSystem implements FileSystem {
    private final String baseUrl;

    /**
     * Constructs a WebDavFileSystem with a base URL.
     * @param baseUrl the base URL for WebDAV access
     */
    public WebDavFileSystem(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public List<FileInfo> listFiles(String path) throws IOException {
        throw new UnsupportedOperationException("WebDAV listFiles not implemented");
    }

    @Override
    public long getLastModified(String path) throws IOException {
        throw new UnsupportedOperationException("WebDAV getLastModified not implemented");
    }

    @Override
    public void copyFile(String sourcePath, String destPath) throws IOException {
        throw new UnsupportedOperationException("WebDAV copyFile not implemented");
    }

    @Override
    public void deleteFile(String path) throws IOException {
        throw new UnsupportedOperationException("WebDAV deleteFile not implemented");
    }

    @Override
    public boolean exists(String path) throws IOException {
        throw new UnsupportedOperationException("WebDAV exists not implemented");
    }

    @Override
    public boolean isDirectory(String path) throws IOException {
        throw new UnsupportedOperationException("WebDAV isDirectory not implemented");
    }
}