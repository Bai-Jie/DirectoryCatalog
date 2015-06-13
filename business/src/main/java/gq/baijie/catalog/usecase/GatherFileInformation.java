package gq.baijie.catalog.usecase;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import gq.baijie.catalog.entity.DirectoryFile;
import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.RegularFile;

public class GatherFileInformation implements UseCase {

    private final DirectoryFile rootDirectoryFile;

    private final Hash.Algorithm[] algorithms;

    public GatherFileInformation(DirectoryFile rootDirectoryFile, Hash.Algorithm[] algorithms) {
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
        Map<Hash.Algorithm, MessageDigest> messageDigestCache = new HashMap<>();
        hashFiles(rootDirectoryFile, messageDigestCache);
    }

    private void hashFiles(
            DirectoryFile directory, Map<Hash.Algorithm, MessageDigest> messageDigestCache) {
        for (final File file : directory.getContent()) {
            if (file instanceof DirectoryFile) {
                hashFiles((DirectoryFile) file, messageDigestCache);
            } else if (file instanceof RegularFile) {
                final RegularFile regularFile = (RegularFile) file;
                for (Hash.Algorithm algorithm : algorithms) {
                    regularFile.getHashs().add(new Hash(algorithm));
                }
                new HashFile(regularFile, messageDigestCache).execute();
            }
        }
    }

}
