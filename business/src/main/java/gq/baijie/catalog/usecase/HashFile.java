package gq.baijie.catalog.usecase;

import org.apache.commons.io.output.NullOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.Hash.Algorithm;

public class HashFile implements UseCase {

    @Nonnull
    private final Path file;

    @Nonnull
    private final Algorithm[] algorithms;

    @Nonnull
    private final Map<Algorithm, MessageDigest> messageDigestCache;

    @Nonnull
    private final Hash[] hashResults;

    /**
     * hash file with appointed algorithms
     *
     * @param file               the regular file will be hashed
     * @param algorithms         the hash algorithms
     * @param messageDigestCache {@link MessageDigest} objects cache for reusing
     * @param hashResults        the hash results of file
     * @throws IllegalArgumentException if hashResults.length < algorithms.length
     */
    public HashFile(
            @Nonnull Path file,
            @Nonnull Algorithm[] algorithms,
            @Nonnull Map<Algorithm, MessageDigest> messageDigestCache,
            @Nonnull Hash[] hashResults) {
        if (hashResults.length < algorithms.length) {
            throw new IllegalArgumentException("hashResults.length < algorithms.length");
        }
        this.file = file;
        this.algorithms = algorithms;
        this.messageDigestCache = messageDigestCache;
        this.hashResults = hashResults;
    }

    public void execute() {
        try {
            execute0();
        } catch (IOException e) {
            throw new RuntimeException("encounter IOException when hash file", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unsupported Hash Algorithm", e);
        }
    }

    private void execute0() throws IOException, NoSuchAlgorithmException {
        if (algorithms.length == 0) {
            return;
        }
        final MessageDigest[] messageDigests = getMessageDigests();
        final DigestOutputStream digestOutputStream = generateDigestOutputStream(messageDigests);

        doHash(digestOutputStream);

        saveHashValues(messageDigests);
    }

    private void doHash(DigestOutputStream digestOutputStream) throws IOException {
        try (FileChannel fileChannel = FileChannel.open(file, StandardOpenOption.READ);
             WritableByteChannel target = Channels.newChannel(digestOutputStream)) {
            final long fileSize = fileChannel.size();
            long count;
            for (long position = 0, maxCount = fileSize;
                    position < fileSize;
                    position += count, maxCount -= count) {
                count = fileChannel.transferTo(position, maxCount, target);
            }
        }
    }

    @Nonnull
    private MessageDigest[] getMessageDigests() throws NoSuchAlgorithmException {
        final MessageDigest[] messageDigests = new MessageDigest[algorithms.length];
        int count = 0;
        for (Algorithm algorithm : algorithms) {
            messageDigests[count++] = toMessageDigest(algorithm);
        }
        return messageDigests;
    }

    @Nonnull
    private MessageDigest toMessageDigest(Algorithm algorithm) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = messageDigestCache.get(algorithm);
        if (messageDigest == null) {
            messageDigest = MessageDigest.getInstance(algorithm.toString());
            messageDigestCache.put(algorithm, messageDigest);
        }
        return messageDigest;
    }

    @Nonnull
    private DigestOutputStream generateDigestOutputStream(@Nonnull MessageDigest[] messageDigests) {
        DigestOutputStream digestOutputStream =
                toDigestOutputStream(NullOutputStream.NULL_OUTPUT_STREAM, messageDigests[0]);
        for (int i = 1; i < messageDigests.length; i++) {
            digestOutputStream = toDigestOutputStream(digestOutputStream, messageDigests[i]);
        }
        return digestOutputStream;
    }

    @Nonnull
    private DigestOutputStream toDigestOutputStream(OutputStream stream, MessageDigest digest) {
        digest.reset();
        return new DigestOutputStream(stream, digest);
    }

    private void saveHashValues(@Nonnull MessageDigest[] messageDigests) {
        int count = 0;
        for (Algorithm algorithm : algorithms) {
            hashResults[count] = new Hash(messageDigests[count].digest(), algorithm);
            count++;
        }
    }

}
