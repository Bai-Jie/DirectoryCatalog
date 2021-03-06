package gq.baijie.catalog.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import gq.baijie.catalog.entity.FileInformation;

public abstract class DirectoryTreeRender {

    private final String mLineBreak;

    public DirectoryTreeRender(String lineBreak) {
        mLineBreak = lineBreak;
    }

    public static DirectoryTreeRender newInstance(boolean hasHorizontalBeforeFile) {
        return newInstance(hasHorizontalBeforeFile, System.getProperty("line.separator"));
    }

    public static DirectoryTreeRender newInstance(boolean hasHorizontalBeforeFile,
            String linebreak) {
        if (hasHorizontalBeforeFile) {
            return new ImplementWithHorizontalBeforeFile(linebreak);
        } else {
            return new ImplementWithoutHorizontalBeforeFile(linebreak);
        }
    }

    public String renderDirectoryTree(TreeNode<FileInformation> tree) {
        List<StringBuilder> lines = renderDirectoryTreeLines(tree);
        StringBuilder sb = new StringBuilder(lines.size() * 20);
        for (StringBuilder line : lines) {
            sb.append(line);
            sb.append(mLineBreak);
        }
        return sb.toString();
    }

    public abstract List<StringBuilder> renderDirectoryTreeLines(
            TreeNode<FileInformation> tree);

    protected static void addSubtree(List<StringBuilder> result, List<StringBuilder> subtree) {
        Iterator<StringBuilder> iterator = subtree.iterator();
        //subtree generated by renderDirectoryTreeLines has at least one line which is tree.getData()
        result.add(iterator.next().insert(0, "├── "));
        while (iterator.hasNext()) {
            result.add(iterator.next().insert(0, "│   "));
        }
    }

    protected static void addLastSubtree(List<StringBuilder> result, List<StringBuilder> subtree) {
        Iterator<StringBuilder> iterator = subtree.iterator();
        //subtree generated by renderDirectoryTreeLines has at least one line which is tree.getData()
        result.add(iterator.next().insert(0, "└── "));
        while (iterator.hasNext()) {
            result.add(iterator.next().insert(0, "    "));
        }
    }

    private static class ImplementWithHorizontalBeforeFile extends DirectoryTreeRender {

        public ImplementWithHorizontalBeforeFile(String linebreak) {
            super(linebreak);
        }

        @Override
        public List<StringBuilder> renderDirectoryTreeLines(TreeNode<FileInformation> tree) {
            List<StringBuilder> result = new LinkedList<>();
            result.add(new StringBuilder().append(tree.getData().getPath().getFileName()));
            Iterator<TreeNode<FileInformation>> iterator = tree.getChildren().iterator();
            while (iterator.hasNext()) {
                List<StringBuilder> subtree = renderDirectoryTreeLines(iterator.next());
                if (iterator.hasNext()) {
                    addSubtree(result, subtree);
                } else {
                    addLastSubtree(result, subtree);
                }
            }
            return result;
        }
    }

    private static class ImplementWithoutHorizontalBeforeFile extends DirectoryTreeRender {

        public ImplementWithoutHorizontalBeforeFile(String linebreak) {
            super(linebreak);
        }

        @Override
        public List<StringBuilder> renderDirectoryTreeLines(TreeNode<FileInformation> tree) {
            List<StringBuilder> result = new LinkedList<>();
            result.add(new StringBuilder().append(tree.getData().getPath().getFileName()));
            Iterator<TreeNode<FileInformation>> iterator = tree.getChildren().iterator();
            while (iterator.hasNext()) {
                List<StringBuilder> fileLines = new LinkedList<>();
                TreeNode<FileInformation> nextDirectorySubtree = null;
                while (iterator.hasNext()) {
                    TreeNode<FileInformation> next = iterator.next();
                    if (!next.getData().isDirectory()) {
                        fileLines.add(new StringBuilder().append(
                                next.getData().getPath().getFileName()));
                    } else {
                        nextDirectorySubtree = next;
                        break;
                    }
                }
                if (nextDirectorySubtree == null) {
                    addLastFileLines(result, fileLines);
                } else {
                    addFileLines(result, fileLines);
                    List<StringBuilder> subtreeLines =
                            renderDirectoryTreeLines(nextDirectorySubtree);
                    if (iterator.hasNext()) {
                        addSubtree(result, subtreeLines);
                    } else {
                        addLastSubtree(result, subtreeLines);
                    }
                }
            }
            return result;
        }

        private void addFileLines(List<StringBuilder> result, List<StringBuilder> fileLines) {
            for (StringBuilder line : fileLines) {
                result.add(line.insert(0, "│   "));
            }
            if (!fileLines.isEmpty()) {  // add a empty line after files
                result.add(new StringBuilder().append('│'));
            }
        }

        private void addLastFileLines(List<StringBuilder> result, List<StringBuilder> fileLines) {
            for (StringBuilder line : fileLines) {
                result.add(line.insert(0, "    "));
            }
            if (!fileLines.isEmpty()) {  // add a empty line after files
                result.add(new StringBuilder());
            }
        }

    }

}
