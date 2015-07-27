package gq.baijie.catalog.storage.v1.util;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import gq.baijie.catalog.entity.DirectoryFile;
import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.RegularFile;

public class FileUtils {

    private static final int ALL_ALGORITHMS_LENGTH = Hash.Algorithm.values().length;

    private static final Comparator<File> ISOSTRUCTURAL_FILE =
            new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (isostructuralEquals(o1, o2)) {
                        return 0;
                    } else {
                        return -1;
                    }
                }

                private boolean isostructuralEquals(File o1, File o2) {
                    return o1 == o2 || ( // ignore UselessParentheses. have it more clear
                            o1.getClass() == o2.getClass() // NOPMD
                                    && isostructuralEquals(o1.getParent(), o2.getParent())
                                    && Objects.equals(o1.getPath(), o2.getPath())
                    );
                }
            };

    /**
     * get hash {@link Hash.Algorithm Algorithms} used in the file
     *
     * @param file {@link RegularFile} or {@link DirectoryFile}
     * @return the used Algorithms in file
     */
    @Nonnull
    public static Hash.Algorithm[] getUsedAlgorithms(File file) {
        Set<Hash.Algorithm> algorithms = EnumSet.noneOf(Hash.Algorithm.class);
        getUsedAlgorithms0(file, algorithms);
        return algorithms.toArray(new Hash.Algorithm[algorithms.size()]);
    }

    /**
     * @param file                {@link RegularFile} or {@link DirectoryFile}
     * @param algorithmsContainer the result which is used Algorithms in file
     * @return if algorithmsContainer.containsAll(ALL_ALGORITHMS)
     */
    private static boolean getUsedAlgorithms0(File file, Set<Hash.Algorithm> algorithmsContainer) {
        if (file instanceof RegularFile) {
            algorithmsContainer.addAll(getUsedAlgorithms1((RegularFile) file));
            return algorithmsContainer.size() == ALL_ALGORITHMS_LENGTH;
        } else if (file instanceof DirectoryFile) {
            for (File child : file.getChildren()) {
                if (getUsedAlgorithms0(child, algorithmsContainer)) {
                    return true;
                }
            }
            return false;
        } else {
            throw new RuntimeException("Shouldn't goto here");
        }
    }

    static Set<Hash.Algorithm> getUsedAlgorithms1(RegularFile file) {
        Set<Hash.Algorithm> algorithms = EnumSet.noneOf(Hash.Algorithm.class);
        for (Map.Entry<Hash.Algorithm, Hash> entry : file.getHashes().entrySet()) {
            if (entry.getValue() != null) {
                algorithms.add(entry.getKey());
            }
        }
        return algorithms;
    }

    @Nonnull
    public static File mergeFiles(@Nonnull File... files) {
        if (files.length == 0) {
            throw new IllegalArgumentException("no files to merge");
        }
        File result = ObjectUtils.clone(files[0]);
        for (int index = 1; index < files.length; index++) {
            mergeFiles(result, files[index]);
        }
        return result;
    }

    public static void mergeFiles(@Nonnull File mergeTo, @Nonnull File beMerged) {
        if (ISOSTRUCTURAL_FILE.compare(mergeTo, beMerged) != 0) {
            throw new IllegalArgumentException("mergeTo and beMerged are not isostructural.");
        }
        mergeFiles0(mergeTo, beMerged);
    }

    /**
     * pre-condition: mergeTo and beMerged are isostructural
     */
    private static void mergeFiles0(@Nonnull File mergeTo, @Nonnull File beMerged) {
        if (mergeTo.getClass() == DirectoryFile.class) {
            mergeDirectoryFiles((DirectoryFile) mergeTo, (DirectoryFile) beMerged);
        } else if (mergeTo.getClass() == RegularFile.class) {
            mergeRegularFiles((RegularFile) mergeTo, (RegularFile) beMerged);
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported File Type:" + mergeTo.getClass());
        }
    }


    /**
     * pre-condition: mergeTo and beMerged are isostructural
     */
    private static void mergeDirectoryFiles(
            @Nonnull DirectoryFile mergeTo, @Nonnull DirectoryFile beMerged) {
        for (File file : beMerged.getChildren()) {
            final File fileInMergeTo = findFirst(mergeTo.getChildren(), file, ISOSTRUCTURAL_FILE);
            if (fileInMergeTo == null) {
                mergeTo.addChild(ObjectUtils.clone(file));
            } else {
                mergeFiles0(fileInMergeTo, file);
            }
        }
    }

    /**
     * pre-condition: mergeTo and beMerged are isostructural
     */
    private static void mergeRegularFiles(
            @Nonnull RegularFile mergeTo, @Nonnull RegularFile beMerged) {
        final Map<Hash.Algorithm, Hash> hashesInMergeTo = mergeTo.getHashes();
        for (Map.Entry<Hash.Algorithm, Hash> entry : beMerged.getHashes().entrySet()) {
            final Hash.Algorithm algorithm = entry.getKey();
            final Hash hashInMergeTo = hashesInMergeTo.get(algorithm);
            final Hash hashInBeMerged = entry.getValue();
            if (hashInMergeTo == null) {
                hashesInMergeTo.put(algorithm, hashInBeMerged);
            } else {
                if (!Objects.equals(hashInMergeTo, hashInBeMerged)) {
                    throw new IllegalStateException(
                            "mergeTo and beMerged have different " + algorithm + " hash value");
                }
            }
        }
    }

    @Nullable
    public static File findFirst(
            @Nonnull Collection<File> files,
            @Nonnull File target,
            @Nonnull Comparator<File> comparator) {
        for (File file : files) {
            if (comparator.compare(target, file) == 0) {
                return file;
            }
        }
        return null;
    }

}
