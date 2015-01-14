package gq.baijie.catalog.util;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static gq.baijie.catalog.test.util.Utils.getPath;
import static gq.baijie.catalog.util.HEX.bytesToHex;
import static org.junit.Assert.assertEquals;

public class HashTest {

    private static final String FILE_REGULAR = "/file_for_hash_test.txt";

    private static final String FILE_REGULAR_MD5 = "45E67B8234E0BF5598D375762126A0B1";

    private static final String FILE_REGULAR_SHA1 = "73DC0615182A133EB20BD5B86F38BD65EDB0DC63";

    private static final String FILE_REGULAR_SHA256 =
            "2D54B171A54415965A111A5C747591B6AD667A480F198CED97EE93607EA1B0A8";

    private static final String FILE_COMPLEX =
            "/file_for_hash_test_unicode：symbols・¶♥㈱∥∞zh中文(Zhōngwén),汉语,漢語jp日本語(にほんご)ko한국어, 조선어.file extension.txt";

    private static final String FILE_COMPLEX_MD5 = "8B227BA1F563E3535BCD9673522C049D";

    private static final String FILE_COMPLEX_SHA1 = "8BC4AD5CBD9815514513FF99356086C75FEB301A";

    private static final String FILE_COMPLEX_SHA256 =
            "3CDAF3BBE70A531418E032AAC790DAB75ACD78041DD7176B5DCEA385F102B536";

    private static MessageDigest sMessageDigestMD5;

    private static MessageDigest sMessageDigestSHA1;

    private static MessageDigest sMessageDigestSHA256;

    @BeforeClass
    public static void initialize() {
        try {
            sMessageDigestMD5 = MessageDigest.getInstance("MD5");
            sMessageDigestSHA1 = MessageDigest.getInstance("SHA-1");
            sMessageDigestSHA256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void reset() {
        sMessageDigestMD5.reset();
        sMessageDigestSHA1.reset();
        sMessageDigestSHA256.reset();
    }

    @Test
    public void testHashRegularFile() throws IOException {
        Path path = getPath(FILE_REGULAR);
        assertEquals(FILE_REGULAR_MD5, bytesToHex(Hash.hashFile(path, sMessageDigestMD5)));
        assertEquals(FILE_REGULAR_SHA1, bytesToHex(Hash.hashFile(path, sMessageDigestSHA1)));
        assertEquals(FILE_REGULAR_SHA256, bytesToHex(Hash.hashFile(path, sMessageDigestSHA256)));
    }

    @Test
    public void testHashComplexFile() throws IOException {
        Path path = getPath(FILE_COMPLEX);
        assertEquals(FILE_COMPLEX_MD5, bytesToHex(Hash.hashFile(path, sMessageDigestMD5)));
        assertEquals(FILE_COMPLEX_SHA1, bytesToHex(Hash.hashFile(path, sMessageDigestSHA1)));
        assertEquals(FILE_COMPLEX_SHA256, bytesToHex(Hash.hashFile(path, sMessageDigestSHA256)));
    }

}
