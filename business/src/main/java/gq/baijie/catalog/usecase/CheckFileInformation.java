package gq.baijie.catalog.usecase;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.DirectoryFile;
import gq.baijie.catalog.entity.File;
import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.RegularFile;

public class CheckFileInformation implements UseCase {

    @Nonnull
    private final DirectoryFile rootDirectoryFile;

    @Nonnull
    private final FileCheckerListener fileCheckerListener;

    public CheckFileInformation(
            @Nonnull DirectoryFile rootDirectoryFile,
            @Nonnull FileCheckerListener fileCheckerListener
    ) {
        this.rootDirectoryFile = rootDirectoryFile;
        this.fileCheckerListener = fileCheckerListener;
    }

    @Override
    public void execute() {
        checkFiles();
    }

    private void checkFiles() {
        final EnumMap<Hash.Algorithm, MessageDigest> messageDigestCache =
                new EnumMap<>(Hash.Algorithm.class);
        checkFiles(rootDirectoryFile, messageDigestCache);
    }

    @Nonnull
    private CheckResult checkFiles(
            @Nonnull DirectoryFile directory,
            @Nonnull Map<Hash.Algorithm, MessageDigest> messageDigestCache
    ) {
        CheckResult result = CheckResult.CONTINUE;
        for (final File file : directory.getChildren()) {
            if (file instanceof DirectoryFile) {
                result = checkFiles((DirectoryFile) file, messageDigestCache);
            } else if (file instanceof RegularFile) {
                result = checkFile((RegularFile) file, messageDigestCache);
            } else {
                throw new UnsupportedOperationException("unknown instance of File:" + file);
            }
            if (result == CheckResult.TERMINATE) {
                break;
            }
        }
        return result;
    }

    @Nonnull
    private CheckResult checkFile(
            @Nonnull final RegularFile file,
            @Nonnull Map<Hash.Algorithm, MessageDigest> messageDigestCache
    ) {
        final Map<Hash.Algorithm, Hash> hashResultContainer = new EnumMap<>(file.getHashes());
        try {
            new HashFile(file.getPath(), hashResultContainer, messageDigestCache).execute();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                return fileCheckerListener.onCheckFileFailed(file, (IOException) e.getCause());
            } else {
                throw e;
            }
        }
        return fileCheckerListener.onFileChecked(file, hashResultContainer);
    }


    public static interface FileCheckerListener {

        @Nonnull
        public CheckResult onCheckFileFailed(
                @Nonnull RegularFile file, @Nonnull IOException exception);

        /**
         * called after the file is checked
         *
         * @param file       the file on check
         * @param realHashes current hash value of the file on the file system.
         */
        @Nonnull
        public CheckResult onFileChecked(@Nonnull RegularFile file,
                @Nonnull Map<Hash.Algorithm, Hash> realHashes);

    }

    public static abstract class SimpleFileCheckerListener implements FileCheckerListener {

        @Nonnull
        @Override
        public final CheckResult onFileChecked(
                @Nonnull RegularFile file, @Nonnull Map<Hash.Algorithm, Hash> realHashes) {
            return onFileChecked(file, realHashes, Objects.equals(file.getHashes(), realHashes));
        }

        /**
         * called after the file is checked
         *
         * @param file       the file on check
         * @param realHashes current hash value of the file on the file system.
         * @param fileOk     true if file.getHashes() equals to realHashes.
         */
        @Nonnull
        public abstract CheckResult onFileChecked(
                @Nonnull RegularFile file,
                @Nonnull Map<Hash.Algorithm, Hash> realHashes,
                boolean fileOk);

    }

    public static enum CheckResult {
        CONTINUE,
        TERMINATE
    }

}
