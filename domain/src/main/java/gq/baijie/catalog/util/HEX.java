// copy http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java#9855338
package gq.baijie.catalog.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HEX {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static final Map<Character, Integer> HEX_TO_INTEGER = new HashMap<>();

    static {
        for (int i = 0; i < HEX_ARRAY.length; i++) {
            HEX_TO_INTEGER.put(HEX_ARRAY[i], i);
        }
    }

    private HEX() {
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException(
                    "illegal hex: length of hex string isn't even number");
        }
        char[] hexChars = hex.toUpperCase(Locale.US).toCharArray();
        byte[] result = new byte[hexChars.length / 2];
        for (int j = 0; j < result.length; j++) {
            int v = hexCharToHalfByte(hexChars[j * 2]);
            v <<= 4;
            v |= hexCharToHalfByte(hexChars[j * 2 + 1]);
            result[j] = (byte) v;
        }
        return result;
    }

    private static int hexCharToHalfByte(char hexChar) {
        Integer result = HEX_TO_INTEGER.get(hexChar);
        if (result == null) {
            throw new IllegalArgumentException("Illegal hex char: " + hexChar);
        }
        return result;
    }

}
