package fr.urouen.sync.sync;

import java.util.ArrayList;
import java.util.List;

public abstract class FileSystemElement {
    protected String name;
    protected long lastModified;

    public abstract void compare(FileSystemElement other);
}

class FileElement extends FileSystemElement {
    public FileElement(String name, long lastModified) {
        this.name = name;
        this.lastModified = lastModified;
    }

    @Override
    public void compare(FileSystemElement other) {
        // Compare les dates de modification des fichiers
        if (this.lastModified != other.lastModified) {
            System.out.println("File " + this.name + " needs synchronization.");
        }
    }
}

class FolderElement extends FileSystemElement {
    private List<FileSystemElement> elements = new ArrayList<>();

    public void addElement(FileSystemElement element) {
        elements.add(element);
    }

    @Override
    public void compare(FileSystemElement other) {
        // Compare récursivement les éléments du dossier
        for (FileSystemElement element : elements) {
            element.compare(other);
        }
    }
}
