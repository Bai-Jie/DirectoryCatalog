package gq.baijie.catalog.entity;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

public class DirectoryFile extends File implements Cloneable {

    @Nonnull
    private List<File> content = new LinkedList<>();

    public DirectoryFile(Path path) {
        super(path);
    }

    @Override
    @Nonnull
    public List<File> getChildren() {
        return Collections.unmodifiableList(content);
    }

    @Override
    public boolean addChild(@Nonnull File child) {
        if (child.getParent() != null) {
            throw new IllegalStateException("child has parent");
        }
        if (content.add(child)) {
            child.setParent(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeChild(@Nonnull File child) {
        if (content.remove(child)) {
            child.setParent(null);
            return true;
        } else {
            return false;
        }
    }

    @Nonnull
    @Override
    public DirectoryFile clone() throws CloneNotSupportedException {
        DirectoryFile clone = (DirectoryFile) super.clone();
        clone.content = new LinkedList<>();
        clone.content.addAll(content);
        return clone;
    }

}
