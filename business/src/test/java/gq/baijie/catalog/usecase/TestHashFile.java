package gq.baijie.catalog.usecase;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import gq.baijie.catalog.entity.Hash;

import static gq.baijie.catalog.test.util.Utils.getPath;
import static gq.baijie.catalog.util.HEX.hexToBytes;
import static org.junit.Assert.assertArrayEquals;

public class TestHashFile {

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

    private static final Hash.Algorithm[] ALGORITHMS =
            {Hash.Algorithm.MD5, Hash.Algorithm.SHA1, Hash.Algorithm.SHA256};


    @Test
    public void testHashRegularFile() throws IOException {
        final Path path = getPath(FILE_REGULAR);
        Map<Hash.Algorithm, Hash> hashes = new EnumMap<>(Hash.Algorithm.class);
        for (Hash.Algorithm algorithm : ALGORITHMS) {
            hashes.put(algorithm, null);
        }
        new HashFile(path, hashes, new HashMap<Hash.Algorithm, MessageDigest>()).execute();
        assertHashValue(FILE_REGULAR_MD5, hashes, Hash.Algorithm.MD5);
        assertHashValue(FILE_REGULAR_SHA1, hashes, Hash.Algorithm.SHA1);
        assertHashValue(FILE_REGULAR_SHA256, hashes, Hash.Algorithm.SHA256);
        System.out.println(hashes);
    }

    @Test
    public void testHashComplexFile() throws IOException {
        final Path path = getPath(FILE_COMPLEX);
        Map<Hash.Algorithm, Hash> hashes = new EnumMap<>(Hash.Algorithm.class);
        for (Hash.Algorithm algorithm : ALGORITHMS) {
            hashes.put(algorithm, null);
        }
        new HashFile(path, hashes, new HashMap<Hash.Algorithm, MessageDigest>()).execute();
        assertHashValue(FILE_COMPLEX_MD5, hashes, Hash.Algorithm.MD5);
        assertHashValue(FILE_COMPLEX_SHA1, hashes, Hash.Algorithm.SHA1);
        assertHashValue(FILE_COMPLEX_SHA256, hashes, Hash.Algorithm.SHA256);
        System.out.println(hashes);
    }

    private static void assertHashValue(
            String expecteds, Map<Hash.Algorithm, Hash> hashes, Hash.Algorithm algorithm) {
        assertArrayEquals(hexToBytes(expecteds), hashes.get(algorithm).getValue());
    }

}
