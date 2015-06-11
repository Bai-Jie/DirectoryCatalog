package gq.baijie.catalog.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HEXTest {

    private static final String HEX_STRING;

    private static final byte[] BYTES = new byte[256];

    static {
        StringBuilder stringBuilder = new StringBuilder(256);
        for (int i = 0; i < BYTES.length; i++) {
            BYTES[i] = (byte) i;
            if (i >= 16) {
                stringBuilder.append(Integer.toString(i, 16));
            } else {
                stringBuilder.append('0').append(Integer.toString(i, 16));
            }
        }
        HEX_STRING = stringBuilder.toString().toUpperCase(Locale.US);
    }

    @Test
    public void testBytesToHex() {
        String resultHex = HEX.bytesToHex(BYTES);
        System.out.println("HEX.bytesToHex(BYTES): " + resultHex);
        assertEquals(HEX_STRING, resultHex);
    }

    @Test
    public void testHexToBytes() {
        byte[] resultBytes = HEX.hexToBytes(HEX_STRING);
        System.out.println("HEX.hexToBytes(HEX_STRING): " + Arrays.toString(resultBytes));
        assertArrayEquals(BYTES, resultBytes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexToBytesIllegalFormat1() {
        HEX.hexToBytes("0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexToBytesIllegalFormat2() {
        HEX.hexToBytes("dca");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexToBytesIllegalFormat3() {
        HEX.hexToBytes("fg");
    }
}
