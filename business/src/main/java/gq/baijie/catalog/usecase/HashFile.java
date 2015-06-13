package gq.baijie.catalog.usecase;

import org.apache.commons.io.output.NullOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.StandardOpenOption;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.Hash.Algorithm;
import gq.baijie.catalog.entity.RegularFile;

public class HashFile implements UseCase {

    private final RegularFile file;

    private final Map<Algorithm, MessageDigest> messageDigestCache;

    public HashFile(
            @Nonnull RegularFile file, @Nonnull Map<Algorithm, MessageDigest> messageDigestCache) {
        this.file = file;
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
        final MessageDigest[] messageDigests = getMessageDigests();
        if (messageDigests.length == 0) {
            return;
        }
        final DigestOutputStream digestOutputStream = generateDigestOutputStream(messageDigests);

        try (FileChannel fileChannel = FileChannel.open(file.getPath(), StandardOpenOption.READ);
             WritableByteChannel target = Channels.newChannel(digestOutputStream)) {
            final long fileSize = fileChannel.size();
            long count;
            for (long position = 0, maxCount = fileSize;
                    position < fileSize;
                    position += count, maxCount -= count) {
                count = fileChannel.transferTo(position, maxCount, target);
            }
        }

        saveHashValues(messageDigests);
    }

    @Nonnull
    private MessageDigest[] getMessageDigests() throws NoSuchAlgorithmException {
        final List<Hash> fileHashs = file.getHashs();
        final MessageDigest[] messageDigests = new MessageDigest[fileHashs.size()];
        int count = 0;
        for (Hash hash : fileHashs) {
            messageDigests[count++] = toMessageDigest(hash.getAlgorithm());
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
        for (Hash hash : file.getHashs()) {
            hash.setValue(messageDigests[count++].digest());
        }
    }

}
