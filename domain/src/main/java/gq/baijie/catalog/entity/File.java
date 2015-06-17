package gq.baijie.catalog.entity;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import gq.baijie.catalog.util.TreeNode;

public abstract class File implements TreeNode<File> {

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

    @Override
    @Nullable
    public DirectoryFile getParent() {
        return parent;
    }

    protected void setParent(@Nullable DirectoryFile parent) {
        this.parent = parent;
    }

    @Override
    @Nonnull
    public List<File> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public void sortChildren(@Nullable Comparator<? super File> c) {
        //do nothing for sorting emptyList
    }

    @Override
    public boolean addChild(@Nonnull File child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeChild(@Nonnull File child) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Nonnull
    public File clone() throws CloneNotSupportedException {
        File clone = (File) super.clone();
        if (clone.getParent() != null) {
            clone.parent = clone.getParent().clone();
        }
        return clone;
    }

}
