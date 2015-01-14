package gq.baijie.catalog.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gq.baijie.catalog.entity.FileInformation;

// hash table's format:
/*
(
Node
empty lines (required)
)+
 */
/*
Node =:
\[(d|D)\]\s+\*+\s+(\d+)         //like "[D] ******** 8"
empty lines (optional)
directory path
files in this directory
 */
/*
files in this directory =:

 */
public class Scanner {

    private static final Pattern DIRECTORY_PATTERN =
            Pattern.compile("^\\[[dD]\\]\\s+\\*+\\s+(\\d+)$");

    private static final Pattern FILE_PATTERN =
            Pattern.compile("^(?<hash>\\w+)\\s+(?<filename>.+)$");

    public static TreeNode<FileInformation> fromHashTable(String table, Path root) {
        java.util.Scanner scanner = new java.util.Scanner(table);
        return scanDirectory(scanner, root);
    }

    public static TreeNode<FileInformation> scanDirectory(java.util.Scanner src, Path root) {
        TreeNode<FileInformation> container = new TreeNode<>(new FileInformation());
        scanDirectory(container, src, 0, root.toAbsolutePath());
        return container.getChildren().get(0); //TODO remove child before this
    }

    private static void scanDirectory(
            TreeNode<FileInformation> parent, java.util.Scanner src, int parentDepth, Path root) {
        Directory nextDirectory = nextDirectory(src);
        if (nextDirectory == null) {
            return;
        }
        FileInformation directoryInformation = new FileInformation()
                .setPath(root.resolve(nextDirectory.directory).normalize())
                .setDirectory(true);
        // find real parent
        for (; parentDepth >= nextDirectory.depth; parentDepth--) {
            parent = parent.getParent();
        }
        TreeNode<FileInformation> current;
        // add current directory
        if (parentDepth + 1 == nextDirectory.depth) {
            current = parent.addChild(directoryInformation);
        } else {
            String message =
                    String.format("parentDepth:%d, depth%d", parentDepth, nextDirectory.depth);
            throw new Error("depth error:" + message);//TODO Error
        }
        addFiles(current, src);
        scanDirectory(current, src, nextDirectory.depth, root);
    }

    private static Directory nextDirectory(java.util.Scanner src) {
        String nextLine = nextNonemptyLine(src);
        if (nextLine == null) {
            return null;
        }
        Matcher matcher = DIRECTORY_PATTERN.matcher(nextLine);
        if (!matcher.matches()) {
            throw new RuntimeException("unkonw title:" + nextLine);//TODO exception
        }
        Directory result = new Directory();
        result.depth = Integer.valueOf(matcher.group(1));
        nextLine = nextNonemptyLine(src);
        if (nextLine == null) {
            throw new RuntimeException("should has directory"); // TODO exception
        }
        result.directory = nextLine;
        return result;
    }

    private static void addFiles(TreeNode<FileInformation> directory, java.util.Scanner src) {
        String directoryPath = directory.getData().getPath().toString();
        while (src.hasNext()) {
            String nextLine = src.nextLine();
            if (!nextLine.isEmpty()) {
                directory.addChild(parseFile(directoryPath, nextLine));
            } else {
                break;
            }
        }
    }

    private static FileInformation parseFile(String directoryPath, String fileLine) {
        Matcher matcher = FILE_PATTERN.matcher(fileLine);
        if (matcher.matches()) {
            return new FileInformation()
                    .setHash(HEX.hexToBytes(matcher.group("hash"))) //TODO constant
                    .setDirectory(false)
                    .setPath(Paths.get(directoryPath, matcher.group("filename")));//TODO ↑
        } else {
            throw new RuntimeException("file format error"); //TODO exception
        }
    }

    private static String nextNonemptyLine(java.util.Scanner src) {
        String nextLine = null;
        while (src.hasNext()) {
            nextLine = src.nextLine();
            if (!nextLine.isEmpty()) {
                break;
            }
        }
        return nextLine;
    }

    private static class Directory {

        public String directory = null;

        public int depth = Integer.MAX_VALUE;
    }


}
