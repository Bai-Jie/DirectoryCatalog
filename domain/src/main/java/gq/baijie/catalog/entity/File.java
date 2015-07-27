package gq.baijie.catalog.entity;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import gq.baijie.catalog.util.TreeNode;

public abstract class File implements TreeNode<File>, Cloneable {

    @Nonnull
    private final Path path;

    @Nullable
    private DirectoryFile parent;

    protected File(Path path) {
        this.path = path;
    }

    ////////////////////////////////////////////////////////////////////////////
    // getters and setters
    ////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////
    // overridden Object methods
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(getPath())
                .append(getChildren())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        File rightHand = (File) obj;
        return Objects.equals(getPath(), rightHand.getPath())
                && Objects.equals(getChildren(), rightHand.getChildren());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("path", getPath())
                .append("children", getChildren())
                .toString();
    }

    @Override
    @Nonnull
    public File clone() throws CloneNotSupportedException {
        File clone = (File) super.clone();
        clone.setParent(null);
        return clone;
    }

}
