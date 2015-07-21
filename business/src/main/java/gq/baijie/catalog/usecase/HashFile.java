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
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.Hash.Algorithm;

public class HashFile implements UseCase {

    @Nonnull
    private final Path file;

    @Nonnull
    private final Map<Algorithm, Hash> hashResults;

    @Nonnull
    private final Map<Algorithm, MessageDigest> messageDigestCache;

    /**
     * hash file with appointed algorithms
     * <pre>
     *     for each algorithm in hashResults
     *         calculate hash value of the file with the algorithm
     *         save the hash value in hashResults
     * </pre>
     *
     * @param file               the regular file will be hashed
     * @param hashResults        the hash algorithms will be used and where to save result in
     * @param messageDigestCache {@link MessageDigest} objects cache for reusing
     * @throws IllegalArgumentException if hashResults.length < algorithms.length
     */
    public HashFile(
            @Nonnull Path file,
            @Nonnull Map<Algorithm, Hash> hashResults,
            @Nonnull Map<Algorithm, MessageDigest> messageDigestCache) {
        this.file = file;
        this.hashResults = hashResults;
        this.messageDigestCache = messageDigestCache;
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
        if (hashResults.isEmpty()) {
            return;
        }
        final Map<Algorithm, MessageDigest> messageDigests = getMessageDigests();
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
    private Map<Algorithm, MessageDigest> getMessageDigests() throws NoSuchAlgorithmException {
        final Map<Algorithm, MessageDigest> messageDigestMap = new EnumMap<>(Algorithm.class);
        for (Algorithm algorithm : hashResults.keySet()) {
            messageDigestMap.put(algorithm, toMessageDigest(algorithm));
        }
        return messageDigestMap;
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
    private DigestOutputStream generateDigestOutputStream(
            @Nonnull Map<Algorithm, MessageDigest> messageDigestsMap) {
        final Collection<MessageDigest> values = messageDigestsMap.values();
        final MessageDigest[] messageDigests = values.toArray(new MessageDigest[values.size()]);
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

    private void saveHashValues(@Nonnull Map<Algorithm, MessageDigest> messageDigests) {
        for (Map.Entry<Algorithm, MessageDigest> entry : messageDigests.entrySet()) {
            hashResults.put(entry.getKey(), new Hash(entry.getValue().digest(), entry.getKey()));
        }
    }

}
