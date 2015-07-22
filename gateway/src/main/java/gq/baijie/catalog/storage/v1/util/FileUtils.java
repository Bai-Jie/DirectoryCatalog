package gq.baijie.catalog.storage.v1.util;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.DirectoryFile;
import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.RegularFile;

public class FileUtils {

    private static final int ALL_ALGORITHMS_LENGTH = Hash.Algorithm.values().length;

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

}
