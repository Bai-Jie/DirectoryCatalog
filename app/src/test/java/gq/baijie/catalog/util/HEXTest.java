package gq.baijie.catalog.util;

import org.junit.Test;

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
        assertEquals(HEX_STRING, HEX.bytesToHex(BYTES));
    }

    @Test
    public void testHexToBytes() {
        assertArrayEquals(BYTES, HEX.hexToBytes(HEX_STRING));
    }
}
