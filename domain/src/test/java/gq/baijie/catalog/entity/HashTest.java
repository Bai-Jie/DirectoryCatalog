package gq.baijie.catalog.entity;

import org.junit.Test;

import gq.baijie.catalog.util.HEX;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HashTest {

    private static final String MD5_HEX = "45E67B8234E0BF5598D375762126A0B1";

    private static final String MD5_HEX2 = "45E67B8234E0Be5598D375762126A0B1";

    private static final String SHA1_HEX = "73DC0615182A133EB20BD5B86F38BD65EDB0DC63";

    private static final String SHA1_HEX2 = "73DC0615182A133EB20BD5B86F38BD65EDB0DC64";

    private static final String SHA256_HEX =
            "2D54B171A54415965A111A5C747591B6AD667A480F198CED97EE93607EA1B0A8";

    private static final String SHA256_HEX2 =
            "3D54B171A54415965A111A5C747591B6AD667A480F198CED97EE93607EA1B0A8";

    // for public Hash(byte[] value, Algorithm algorithm)
    @Test
    public void testConstructorWithByteArrayAlgorithm() {
        Hash hash;
        hash = new Hash(HEX.hexToBytes(MD5_HEX), Hash.Algorithm.MD5);
        System.out.println(hash);
        hash = new Hash(HEX.hexToBytes(SHA1_HEX), Hash.Algorithm.SHA1);
        System.out.println(hash);
        hash = new Hash(HEX.hexToBytes(SHA256_HEX), Hash.Algorithm.SHA256);
        System.out.println(hash);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithByteArrayAlgorithmNull1() {
        Hash hash = new Hash(null, Hash.Algorithm.MD5);
        System.out.println(hash);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithByteArrayAlgorithmNull2() {
        Hash hash = new Hash(HEX.hexToBytes(MD5_HEX), null);
        System.out.println(hash);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithByteArrayAlgorithmNull3() {
        Hash hash = new Hash(null, null);
        System.out.println(hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithByteArrayAlgorithmConsistent1() {
        Hash hash = new Hash(HEX.hexToBytes("00" + MD5_HEX), Hash.Algorithm.MD5);
        System.out.println(hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithByteArrayAlgorithmConsistent2() {
        Hash hash = new Hash(HEX.hexToBytes("00" + SHA1_HEX), Hash.Algorithm.SHA1);
        System.out.println(hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithByteArrayAlgorithmConsistent3() {
        Hash hash = new Hash(HEX.hexToBytes("00" + SHA256_HEX), Hash.Algorithm.SHA256);
        System.out.println(hash);
    }

    // for public Hash(byte[] value)

    @Test
    public void testConstructorWithByteArray() {
        Hash hash;
        hash = new Hash(HEX.hexToBytes(MD5_HEX));
        assertEquals(Hash.Algorithm.MD5, hash.getAlgorithm());
        hash = new Hash(HEX.hexToBytes(SHA1_HEX));
        assertEquals(Hash.Algorithm.SHA1, hash.getAlgorithm());
        hash = new Hash(HEX.hexToBytes(SHA256_HEX));
        assertEquals(Hash.Algorithm.SHA256, hash.getAlgorithm());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithByteArrayNull() {
        Hash hash = new Hash(null);
        System.out.println(hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithByteArrayErrorValue1() {
        Hash hash = new Hash(HEX.hexToBytes("00" + MD5_HEX));
        System.out.println(hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithByteArrayErrorValue2() {
        Hash hash = new Hash(HEX.hexToBytes("00" + SHA1_HEX));
        System.out.println(hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithByteArrayErrorValue3() {
        Hash hash = new Hash(HEX.hexToBytes("00" + SHA256_HEX));
        System.out.println(hash);
    }

    // for public boolean equals(Object obj)

    @Test
    public void testEquals() {
        Hash hash1, hash2;

        hash1 = new Hash(HEX.hexToBytes(MD5_HEX), Hash.Algorithm.MD5);
        hash2 = new Hash(HEX.hexToBytes(MD5_HEX));
        assertTrue(hash1.equals(hash2));
        hash2 = new Hash(HEX.hexToBytes(MD5_HEX2));
        assertFalse(hash1.equals(hash2));
        hash2 = new Hash(HEX.hexToBytes(SHA1_HEX));
        assertFalse(hash1.equals(hash2));

        hash1 = new Hash(HEX.hexToBytes(SHA1_HEX), Hash.Algorithm.SHA1);
        hash2 = new Hash(HEX.hexToBytes(SHA1_HEX));
        assertTrue(hash1.equals(hash2));
        hash2 = new Hash(HEX.hexToBytes(SHA1_HEX2));
        assertFalse(hash1.equals(hash2));
        hash2 = new Hash(HEX.hexToBytes(SHA256_HEX));
        assertFalse(hash1.equals(hash2));

        hash1 = new Hash(HEX.hexToBytes(SHA256_HEX), Hash.Algorithm.SHA256);
        hash2 = new Hash(HEX.hexToBytes(SHA256_HEX));
        assertTrue(hash1.equals(hash2));
        hash2 = new Hash(HEX.hexToBytes(SHA256_HEX2));
        assertFalse(hash1.equals(hash2));
        hash2 = new Hash(HEX.hexToBytes(MD5_HEX));
        assertFalse(hash1.equals(hash2));
    }

}
