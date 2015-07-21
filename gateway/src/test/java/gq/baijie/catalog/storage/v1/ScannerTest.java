package gq.baijie.catalog.storage.v1;

import org.junit.Test;

import java.nio.file.Path;

import javax.annotation.Nonnull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gq.baijie.catalog.entity.RegularFile;
import gq.baijie.catalog.util.HEX;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
public class ScannerTest {

    public static final String DIRECTORY_PATH_EXAMPLE = "/d/temp/sample/";

    public static final String FILE_INFORMATION_EXAMPLE_FILENAME =
            "commons-lang3-3.4.jar";

    public static final String FILE_INFORMATION_EXAMPLE_NULL =
            "null commons-lang3-3.4.jar";

    public static final String FILE_INFORMATION_EXAMPLE_MD5 =
            "8667A442EE77E509FBE8176B94726EB2 commons-lang3-3.4.jar";

    public static final String FILE_INFORMATION_EXAMPLE_MD5_HASH =
            "8667A442EE77E509FBE8176B94726EB2";

    public static final String FILE_INFORMATION_EXAMPLE_SHA1 =
            "5FE28B9518E58819180A43A850FBC0DD24B7C050 commons-lang3-3.4.jar";

    public static final String FILE_INFORMATION_EXAMPLE_SHA1_HASH =
            "5FE28B9518E58819180A43A850FBC0DD24B7C050";

    public static final String FILE_INFORMATION_EXAMPLE_SHA256 =
            "734C8356420CC8E30C795D64FD1FCD5D44EA9D90342A2CC3262C5158FBC6D98B commons-lang3-3.4.jar";

    public static final String FILE_INFORMATION_EXAMPLE_SHA256_HASH =
            "734C8356420CC8E30C795D64FD1FCD5D44EA9D90342A2CC3262C5158FBC6D98B";

    @Test
    public void testParseFileNull() {
        RegularFile regularFile =
                Scanner.parseFile(DIRECTORY_PATH_EXAMPLE, FILE_INFORMATION_EXAMPLE_NULL);
        assertTrue(regularFile.getHashs().isEmpty());
        assertEquals(FILE_INFORMATION_EXAMPLE_FILENAME, getFileName(regularFile));
        System.out.println(regularFile);
    }

    @Test
    public void testParseFileMD5() {
        RegularFile regularFile =
                Scanner.parseFile(DIRECTORY_PATH_EXAMPLE, FILE_INFORMATION_EXAMPLE_MD5);
        assertHashEquals(FILE_INFORMATION_EXAMPLE_MD5_HASH, getFirstHashValue(regularFile));
        assertEquals(FILE_INFORMATION_EXAMPLE_FILENAME, getFileName(regularFile));
        System.out.println(regularFile);
    }

    @Test
    public void testParseFileSHA1() {
        RegularFile regularFile =
                Scanner.parseFile(DIRECTORY_PATH_EXAMPLE, FILE_INFORMATION_EXAMPLE_SHA1);
        assertHashEquals(FILE_INFORMATION_EXAMPLE_SHA1_HASH, getFirstHashValue(regularFile));
        assertEquals(FILE_INFORMATION_EXAMPLE_FILENAME, getFileName(regularFile));
        System.out.println(regularFile);
    }

    @Test
    public void testParseFileSHA256() {
        RegularFile regularFile =
                Scanner.parseFile(DIRECTORY_PATH_EXAMPLE, FILE_INFORMATION_EXAMPLE_SHA256);
        assertHashEquals(FILE_INFORMATION_EXAMPLE_SHA256_HASH, getFirstHashValue(regularFile));
        assertEquals(FILE_INFORMATION_EXAMPLE_FILENAME, getFileName(regularFile));
        System.out.println(regularFile);
    }

    private static void assertHashEquals(String expected, byte[] actual) {
        assertArrayEquals(HEX.hexToBytes(expected), actual);
    }

    private static byte[] getFirstHashValue(@Nonnull RegularFile regularFile) {
        return regularFile.getHashs().get(0).getValue();
    }

    private static String getFileName(@Nonnull RegularFile regularFile) {
        Path fileName = regularFile.getPath().getFileName();
        return fileName != null ? fileName.toString() : null;
    }

}
