package gq.baijie.catalog.entity;

import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class File {

    @Nonnull
    private final Path path;

    @Nullable
    private DirectoryFile parent;

    protected File(Path path) {
        this.path = path;
    }

    @Nonnull
    public Path getPath() {
        return path;
    }

    @Nullable
    public DirectoryFile getParent() {
        return parent;
    }

    public void setParent(@Nullable DirectoryFile parent) {
        this.parent = parent;
    }
}
