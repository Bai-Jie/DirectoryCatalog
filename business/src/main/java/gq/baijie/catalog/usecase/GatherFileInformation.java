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
        final Hash[] hashResultContainer = new Hash[algorithms.length];
        hashFiles(rootDirectoryFile, messageDigestCache, hashResultContainer);
    }

    private void hashFiles(
            @Nonnull DirectoryFile directory,
            @Nonnull Map<Hash.Algorithm, MessageDigest> messageDigestCache,
            @Nonnull Hash[] hashResultContainer) {
        for (final File file : directory.getContent()) {
            if (file instanceof DirectoryFile) {
                hashFiles((DirectoryFile) file, messageDigestCache, hashResultContainer);
            } else if (file instanceof RegularFile) {
                new HashFile(file.getPath(), algorithms, messageDigestCache, hashResultContainer)
                        .execute();
                for (Hash hash : hashResultContainer) {
                    ((RegularFile) file).getHashs().add(hash);
                }
            } else {
                throw new UnsupportedOperationException("unknown instance of File:" + file);
            }
        }
    }

}
