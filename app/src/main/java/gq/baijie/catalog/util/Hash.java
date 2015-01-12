package gq.baijie.catalog.util;

import org.apache.commons.io.output.NullOutputStream;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

public class Hash {

    public static byte[] hashFile(Path file, MessageDigest messageDigest) throws IOException {
        messageDigest.reset();
        final DigestOutputStream digestOutputStream =
                new DigestOutputStream(NullOutputStream.NULL_OUTPUT_STREAM, messageDigest);
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
        return messageDigest.digest();
    }
}
