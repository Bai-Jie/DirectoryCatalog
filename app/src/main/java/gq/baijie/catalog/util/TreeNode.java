// copy from http://stackoverflow.com/questions/3522454/java-tree-data-structure#17490124
package gq.baijie.catalog.util;

import java.util.LinkedList;
import java.util.List;

public class TreeNode<E> implements Cloneable {

    E data;

    TreeNode<E> parent;

    List<TreeNode<E>> children;

    public TreeNode(E data) {
        this.data = data;
        this.children = new LinkedList<>();
    }

    public E getData() {
        return data;
    }


    public TreeNode<E> getParent() {
        return parent;
    }

    public List<TreeNode<E>> getChildren() {
        return children;
    }

    public TreeNode<E> addChild(E child) {
        TreeNode<E> childNode = new TreeNode<E>(child);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "data=" + data +
                ", children=" + children +
                '}';
    }

    /**
     * Returns a shallow copy of this {@code TreeNode}.
     * (The elements themselves and parent are not cloned.)
     *
     * @return a shallow copy of this {@code TreeNode} instance
     */
    @Override
    protected TreeNode<E> clone() {
        // shallow clone with data and parent
        TreeNode<E> clone = superClone();
        // clone children
        LinkedList<TreeNode<E>> cloneChildren = new LinkedList<>();
        for (TreeNode<E> child : children) {
            cloneChildren.add(child.clone());
        }
        clone.children = cloneChildren;
        return clone;
    }

    @SuppressWarnings("unchecked")
    private TreeNode<E> superClone() {
        try {
            return (TreeNode<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

}
