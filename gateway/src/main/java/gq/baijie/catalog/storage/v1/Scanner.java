package gq.baijie.catalog.storage.v1;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gq.baijie.catalog.entity.DirectoryFile;
import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.RegularFile;
import gq.baijie.catalog.util.HEX;

// hash table's format:
/*

(
Node
empty lines (required)
)+


Node =:
\[(d|D)\]\s+\*+\s+(\d+)         //like "[D] ******** 8"
empty lines (optional)
directory path
(file information)+             //files in this directory


file information =:             //one file one line
[(hash value of file|null)]\s+[filename]

 */
public class Scanner {

    private static final Pattern DIRECTORY_PATTERN =
            Pattern.compile("^\\[[dD]\\]\\s+\\*+\\s+(\\d+)$");

    private static final Pattern FILE_PATTERN =
            Pattern.compile("^(?<hash>\\w+)\\s+(?<filename>.+)$");

    public static File fromHashTable(String table, Path root) {
        java.util.Scanner scanner = new java.util.Scanner(table);
        return scanDirectory(scanner, root);
    }

    public static File scanDirectory(java.util.Scanner src, Path root) {
        DirectoryFile container = new DirectoryFile(root);
        scanDirectory(container, src, 0, root.toAbsolutePath());
        File file = container.getChildren().get(0);
        container.removeChild(file);
        return file;
    }

    private static void scanDirectory(
            DirectoryFile parent, java.util.Scanner src, int parentDepth, Path root) {
        Directory nextDirectory = nextDirectory(src);
        if (nextDirectory == null) {
            return;
        }
        DirectoryFile directoryInformation =
                new DirectoryFile(root.resolve(nextDirectory.directory).normalize());
        // find real parent
        for (; parentDepth >= nextDirectory.depth; parentDepth--) {
            assert parent != null;
            parent = parent.getParent();
        }
        DirectoryFile current;
        // add current directory
        if (parentDepth + 1 == nextDirectory.depth) {
            assert parent != null;
            parent.addChild(directoryInformation);
            current = directoryInformation;
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
        result.depth = Integer.parseInt(matcher.group(1));
        nextLine = nextNonemptyLine(src);
        if (nextLine == null) {
            throw new RuntimeException("should has directory"); // TODO exception
        }
        result.directory = nextLine;
        return result;
    }

    private static void addFiles(DirectoryFile directory, java.util.Scanner src) {
        String directoryPath = directory.getPath().toString();
        while (src.hasNext()) {
            String nextLine = src.nextLine();
            if (!nextLine.isEmpty()) {
                directory.addChild(parseFile(directoryPath, nextLine));
            } else {
                break;
            }
        }
    }

    static RegularFile parseFile(String directoryPath, String fileLine) {
        Matcher matcher = FILE_PATTERN.matcher(fileLine);
        if (matcher.matches()) {
            RegularFile file = new RegularFile(
                    Paths.get(directoryPath, matcher.group("filename")));//TODO constant?
            String hex = matcher.group("hash").toUpperCase(Locale.US);//TODO constant
            if (!"NULL".equals(hex)) {
                file.getHashes().add(new Hash(HEX.hexToBytes(hex)));
            }
            return file;
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
