package gq.baijie.catalog.storage.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleImporter {

    @Nonnull
    private final String mSource;

    private State state = State.BEFORE_PARSE;

    private String mDirectoryTree;

    @Nonnull
    private final List<HashTable> mHashTables = new LinkedList<>();

    public SimpleImporter(@Nonnull String source) {
        mSource = source;
    }

    private void setDirectoryTree(@Nonnull String directoryTree) {
        if (mDirectoryTree != null) {
            throw new IllegalStateException("have already set mDirectoryTree.");
        }
        mDirectoryTree = directoryTree;
    }

    @Nonnull
    public String getDirectoryTree() {
        if (state != State.AFTER_PARSE) {
            throw new IllegalStateException("haven't parsed.");
        }
        return mDirectoryTree;
    }

    @Nonnull
    public List<HashTable> getHashTables() {
        if (state != State.AFTER_PARSE) {
            throw new IllegalStateException("haven't parsed.");
        }
        return mHashTables;
    }

    public void parse() {
        if (state == State.AFTER_PARSE) {
            throw new IllegalStateException("have parsed.");
        }

        final List<Head> heads = findHeads(mSource);

        checkHeads(heads);
        Collections.sort(heads, Head.POSTION_COMPARATOR);

        //save tables
        //save tables except the last one
        for (int index = 0; index < heads.size() - 1; index++) {
            final Head head1 = heads.get(index);
            final Head head2 = heads.get(index + 1);
            final String table = mSource.substring(head1.getEndPosition(), head2.getStartPostion());
            saveTable(head1, table);
        }
        //save last one table
        {
            final Head lastHead = heads.get(heads.size() - 1);
            final String table = mSource.substring(lastHead.getEndPosition());
            saveTable(lastHead, table);
        }

        state = State.AFTER_PARSE;
    }

    private static ArrayList<Head> findHeads(String source) {
        final ArrayList<Head> heads = new ArrayList<>();
        Matcher matcher = DirectoryTreeHead.PATTERN_HEAD_DIRECTORY_TREE.matcher(source);
        while (matcher.find()) {
            final DirectoryTreeHead directoryTreeHead = new DirectoryTreeHead();
            directoryTreeHead.setPositions(matcher.start(), matcher.end());
            heads.add(directoryTreeHead);
        }
        matcher = HashTableHead.PATTERN_HEAD_HASH_TABLE.matcher(source);
        while (matcher.find()) {
            final HashTableHead hashTableHead = new HashTableHead();
            hashTableHead.setPositions(matcher.start(), matcher.end());
            hashTableHead.setAlgorithm(matcher.group(HashTableHead.CAPTURING_GROUP_NAME_ALGORITHM));
            heads.add(hashTableHead);
        }
        return heads;
    }

    private static void checkHeads(List<Head> heads) {
        //shouldn't be empty
        if (heads.isEmpty()) {
            throw new IllegalStateException("heads shouldn't be empty.");
        }
        //have & only have one DirectoryTreeHead
        DirectoryTreeHead firstDirectoryTreeHead = null;
        for (Head head : heads) {
            if (head instanceof DirectoryTreeHead) {
                if (firstDirectoryTreeHead == null) {
                    firstDirectoryTreeHead = (DirectoryTreeHead) head;
                } else {
                    throw new IllegalStateException("heads should have only one DirectoryTreeHead");
                }
            }
        }
        if (firstDirectoryTreeHead == null) {
            throw new IllegalStateException("heads should have at least one DirectoryTreeHead");
        }
    }

    private void saveTable(@Nonnull Head tableHead, @Nonnull String table) {
        if (tableHead instanceof DirectoryTreeHead) {
            setDirectoryTree(table);
        } else if (tableHead instanceof HashTableHead) {
            final HashTable hashTable = new HashTable();
            hashTable.setHashTable(table);
            hashTable.setAlgorithm(((HashTableHead) tableHead).getAlgorithm());
            mHashTables.add(hashTable);
        } else {
            throw new UnsupportedOperationException("Unsupported Head type.");
        }
    }

    public static class HashTable {

        private String mHashTable;

        private String mAlgorithm;

        @Nonnull
        public String getHashTable() {
            return mHashTable;
        }

        public void setHashTable(@Nonnull String hashTable) {
            mHashTable = hashTable;
        }

        @Nullable
        public String getAlgorithm() {
            return mAlgorithm;
        }

        public void setAlgorithm(@Nullable String algorithm) {
            mAlgorithm = algorithm;
        }
    }

    private static enum State {
        BEFORE_PARSE, AFTER_PARSE
    }

    private abstract static class Head {

        static final Comparator<Head> POSTION_COMPARATOR = new Comparator<Head>() {
            @Override
            public int compare(Head o1, Head o2) {
                final int deltaStartPosition = o1.getStartPostion() - o2.getStartPostion();
                final int deltaEndPosition = o1.getEndPosition() - o2.getEndPosition();
                return deltaStartPosition + deltaEndPosition;
                //this is double average value of deltaStartPosition and deltaEndPosition
            }
        };

        private int mStartPostion = -1;

        private int mEndPosition = -1;

        public void setPositions(int startPostion, int endPosition) {
            //TODO use Preconditions class
            if (startPostion < 0 || endPosition < 0) {
                throw new IllegalArgumentException("startPostion < 0 || endPosition < 0");
            }
            if (startPostion > endPosition) {
                throw new IllegalArgumentException("startPostion > endPosition");
            }
            mStartPostion = startPostion;
            mEndPosition = endPosition;
        }

        public int getStartPostion() {
            return mStartPostion;
        }

        public int getEndPosition() {
            return mEndPosition;
        }
    }

    private static class DirectoryTreeHead extends Head {

        static final Pattern PATTERN_HEAD_DIRECTORY_TREE = Pattern.compile(
                String.format("^%s$\\s{0,2}^%s$", Exporter.HEAD_DIRECTORY_TREE,
                        Exporter.UNDERLINED),
                Pattern.MULTILINE);
    }

    private static class HashTableHead extends Head {

        static final String CAPTURING_GROUP_NAME_ALGORITHM = "algorithm";

        static final Pattern PATTERN_HEAD_HASH_TABLE = Pattern.compile(
                String.format("^%s(?:\\s*\\((<%s>.*)\\))?$\\s{0,2}^%s$",
                        Exporter.HEAD_HASH_TABLE,
                        CAPTURING_GROUP_NAME_ALGORITHM,
                        Exporter.UNDERLINED),
                Pattern.MULTILINE);

        private String mAlgorithm;

        @Nullable
        public String getAlgorithm() {
            return mAlgorithm;
        }

        public void setAlgorithm(@Nullable String algorithm) {
            mAlgorithm = algorithm;
        }
    }


}
