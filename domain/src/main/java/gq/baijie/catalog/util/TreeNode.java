package gq.baijie.catalog.util;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TreeNode<E extends TreeNode<E>> {

    @Nullable
    public TreeNode<E> getParent();

    /**
     *
     * @return unmodifiable list of children
     */
    @Nonnull
    public List<E> getChildren();

    /**
     *
     * @param child
     * @return true if this collection changed as a result of the call
     */
    public boolean addChild(@Nonnull E child);

    /**
     *
     * @param child
     * @return true if {@link #getChildren()} contained the specified element
     */
    public boolean removeChild(@Nonnull E child);

}
