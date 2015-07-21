package gq.baijie.catalog.usecase;

import java.security.MessageDigest;
import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.DirectoryFile;
import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.RegularFile;

public class GatherFileInformation implements UseCase {

    @Nonnull
    private final DirectoryFile rootDirectoryFile;

    @Nonnull
    private final Hash.Algorithm[] algorithms;

    public GatherFileInformation(
            @Nonnull DirectoryFile rootDirectoryFile, @Nonnull Hash.Algorithm[] algorithms) {
        this.rootDirectoryFile = rootDirectoryFile;
        this.algorithms = algorithms.clone();
    }

    @Override
    public void execute() {
        new ScanFileSystem(rootDirectoryFile).execute();
        hashFiles();
    }

    private void hashFiles() {
        if (algorithms.length == 0) {
            return;
        }
        final Map<Hash.Algorithm, MessageDigest> messageDigestCache =
                new EnumMap<>(Hash.Algorithm.class);
        hashFiles(rootDirectoryFile, messageDigestCache);
    }

    private void hashFiles(
            @Nonnull DirectoryFile directory,
            @Nonnull Map<Hash.Algorithm, MessageDigest> messageDigestCache) {
        for (final File file : directory.getChildren()) {
            if (file instanceof DirectoryFile) {
                hashFiles((DirectoryFile) file, messageDigestCache);
            } else if (file instanceof RegularFile) {
                final Map<Hash.Algorithm, Hash> hashes = ((RegularFile) file).getHashes();
                for (Hash.Algorithm algorithm : algorithms) {
                    hashes.put(algorithm, null);
                }
                new HashFile(file.getPath(), hashes, messageDigestCache).execute();
            } else {
                throw new UnsupportedOperationException("unknown instance of File:" + file);
            }
        }
    }

}
