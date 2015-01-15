package gq.baijie.catalog.util;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import gq.baijie.catalog.entity.FileInformation;

public class Printer {

    private String mLineBreak = System.getProperty("line.separator");

    private TreeNode<FileInformation> mDirectoryTree;

    public static final Comparator<TreeNode<FileInformation>> DIRECTORY_TREE_COMPARATOR =
            (o1, o2) -> o1.getData().isDirectory() ?
                    o2.getData().isDirectory() ? 0 : 1 :
                    o2.getData().isDirectory() ? -1 : 0;

    public Printer setLineBreak(String lineBreak) {
        mLineBreak = lineBreak;
        return this;
    }

    public Printer setDirectoryTree(TreeNode<FileInformation> directoryTree) {
        mDirectoryTree = directoryTree.clone();
        sortDirectoryTree(mDirectoryTree);
        return this;
    }

    private static void sortDirectoryTree(TreeNode<FileInformation> directoryTree) {
        List<TreeNode<FileInformation>> children = directoryTree.getChildren();
        children.forEach(gq.baijie.catalog.util.Printer::sortDirectoryTree);
        Collections.sort(children, DIRECTORY_TREE_COMPARATOR);
    }

    public String printHash() {
        StringBuilder stringBuilder = new StringBuilder();
        printHash(stringBuilder);
        return stringBuilder.toString();
    }

    public void printHash(StringBuilder out) {
        printHash(mDirectoryTree, out, 1, mDirectoryTree.getData().getPath());
    }

    private void printHash(
            TreeNode<FileInformation> tree, StringBuilder out, int depth, Path root) {
        FileInformation fileInformation = tree.getData();
        if (fileInformation.isDirectory()) {
            out.append(mLineBreak);
            out.append("[D] ");
            for (long count = 1; count <= depth; count++) {
                out.append('*');
            }
            out.append(' ');
            out.append(String.valueOf(depth));
            out.append(mLineBreak);
            String directoryPath = root.relativize(fileInformation.getPath()).toString();
            if (directoryPath.isEmpty()) {
                directoryPath = ".";
            }
            out.append(directoryPath);
            out.append(mLineBreak);

            for (TreeNode<FileInformation> subtree : tree.getChildren()) {
                printHash(subtree, out, depth + 1, root);
            }
        } else {
            out.append(fileInformation.getHashAsHex());
            out.append(' ');
            out.append(fileInformation.getPath().getFileName());
            out.append(mLineBreak);
        }
    }

    public String renderDirectoryTree() {
        return DirectoryTreeRender.newInstance(false, mLineBreak)
                .renderDirectoryTree(mDirectoryTree);
    }

}
