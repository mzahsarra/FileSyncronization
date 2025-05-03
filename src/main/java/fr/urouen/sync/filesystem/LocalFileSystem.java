package fr.urouen.sync.filesystem;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements FileSystem for local file operations.
 */
public class LocalFileSystem implements FileSystem {

    @Override
    public List<FileInfo> listFiles(String path) throws IOException {
        try (var stream = Files.list(Paths.get(path))) {
            return stream.map(p -> new FileInfo(p.getFileName().toString(), Files.isDirectory(p)))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public long getLastModified(String path) throws IOException {
        return Files.getLastModifiedTime(Paths.get(path)).toMillis();
    }

    @Override
    public void copyFile(String sourcePath, String destPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void deleteFile(String path) throws IOException {
        Files.deleteIfExists(Paths.get(path));
    }

    @Override
    public boolean exists(String path) throws IOException {
        return Files.exists(Paths.get(path));
    }

    @Override
    public boolean isDirectory(String path) throws IOException {
        return Files.isDirectory(Paths.get(path));
    }
}