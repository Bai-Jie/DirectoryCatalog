package gq.baijie.catalog.controllor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Importer {

    private static final Pattern PATTERN_HEAD_DIRECTORY_TREE = Pattern.compile(
            String.format("^%s$\\s{0,2}^%s$", Exporter.HEAD_DIRECTORY_TREE, Exporter.UNDERLINED),
            Pattern.MULTILINE);

    private static final Pattern PATTERN_HEAD_HASH_TABLE = Pattern.compile(
            String.format("^%s$\\s{0,2}^%s$", Exporter.HEAD_HASH_TABLE, Exporter.UNDERLINED),
            Pattern.MULTILINE);

    private String mSource;

    private String mDirectoryTree;

    private String mHashTable;

    public Importer setSource(String source) {
        mSource = source;
        mDirectoryTree = null;
        mHashTable = null;
        return this;
    }

    public String getDirectoryTree() {
        if (mDirectoryTree == null) {
            parseSource();
        }
        return mDirectoryTree;
    }

    public String getHashTable() {
        if (mHashTable == null) {
            parseSource();
        }
        return mHashTable;
    }

    private void parseSource() {
        if (mSource == null) {
            throw new IllegalStateException("haven't set setSource.");
        }
        Matcher matcherDirectoryTree = PATTERN_HEAD_DIRECTORY_TREE.matcher(mSource);
        Matcher matcherHashTable = PATTERN_HEAD_HASH_TABLE.matcher(mSource);
        Integer endOfHeadDirectoryTree = null,
                startOfHeadHashTable = null,
                endOfHeadHashTable = null;
        while (matcherDirectoryTree.find()) {
            if (endOfHeadDirectoryTree == null) {
                endOfHeadDirectoryTree = matcherDirectoryTree.end();
            } else {
                throw new Error("too many Directory Tree Head");//TODO customer exception
            }
        }
        while (matcherHashTable.find()) {
            if (startOfHeadHashTable == null/* && endOfHeadHashTable == null*/) {
                startOfHeadHashTable = matcherHashTable.start();
                endOfHeadHashTable = matcherHashTable.end();
            } else {
                throw new Error("too many Hash Table Head");//TODO customer exception
            }
        }
        if (endOfHeadDirectoryTree == null) {
            throw new Error("Can't find Directory Tree Head.");
        }
        if (startOfHeadHashTable == null/* || endOfHeadHashTable == null*/) {
            throw new Error("Can't find Directory Tree Head.");
        }
        mDirectoryTree = mSource.substring(endOfHeadDirectoryTree, startOfHeadHashTable);
        mHashTable = mSource.substring(endOfHeadHashTable);
    }
}
