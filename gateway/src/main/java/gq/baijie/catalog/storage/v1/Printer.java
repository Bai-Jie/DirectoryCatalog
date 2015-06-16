package gq.baijie.catalog.storage.v1;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import gq.baijie.catalog.entity.DirectoryFile;
import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.RegularFile;
import gq.baijie.catalog.util.HEX;

public class Printer {

    private String mLineBreak = System.getProperty("line.separator");

    private File mFile;

    public static final Comparator<File> DIRECTORY_TREE_COMPARATOR = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            return o1 instanceof DirectoryFile ?
                    o2 instanceof DirectoryFile ? 0 : 1 :
                    o2 instanceof DirectoryFile ? -1 : 0;
        }
    };

    public Printer setLineBreak(String lineBreak) {
        mLineBreak = lineBreak;
        return this;
    }

    public Printer setFile(File file) {
        try {
            mFile = file.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Can't clone file", e);
        }
        sortDirectoryTree(mFile);
        return this;
    }

    private static void sortDirectoryTree(File directoryTree) {
        List<File> children = directoryTree.getChildren();
        for (File file : children) {
            sortDirectoryTree(file);
        }
        Collections.sort(children, DIRECTORY_TREE_COMPARATOR);
    }

    public String printHash() {
        StringBuilder stringBuilder = new StringBuilder();
        printHash(stringBuilder);
        return stringBuilder.toString();
    }

    public void printHash(StringBuilder out) {
        printHash(mFile, out, 1, mFile.getPath());
    }

    private void printHash(File tree, StringBuilder out, int depth, Path root) {
        if (tree instanceof DirectoryFile) {
            out.append(mLineBreak);
            out.append("[D] ");
            for (long count = 1; count <= depth; count++) {
                out.append('*');
            }
            out.append(' ');
            out.append(String.valueOf(depth));
            out.append(mLineBreak);
            String directoryPath = root.relativize(tree.getPath()).toString();
            if (directoryPath.isEmpty()) {
                directoryPath = ".";
            }
            out.append(directoryPath);
            out.append(mLineBreak);

            for (File subtree : tree.getChildren()) {
                printHash(subtree, out, depth + 1, root);
            }
        } else {
            out.append(getRegularFileHashAsHex((RegularFile) tree));
            out.append(' ');
            out.append(tree.getPath().getFileName());
            out.append(mLineBreak);
        }
    }

    private String getRegularFileHashAsHex(RegularFile regularFile) {
        final List<Hash> fileHashs = regularFile.getHashs();
        if (fileHashs.size() > 1) {
            throw new UnsupportedOperationException();//TODO
        } else {
            return fileHashs.size() == 1 ? HEX.bytesToHex(fileHashs.get(0).getValue()) : null;
        }
    }

    public String renderDirectoryTree() {
        return DirectoryTreeRender.newInstance(false, mLineBreak).renderDirectoryTree(mFile);
    }

}
