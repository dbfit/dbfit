package dbfit.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.apache.commons.codec.binary.Hex;

public class BinaryTest {

    @Test
    public void canParseSingleDigitHex() throws Exception {
        String s = "0A";
        byte[] result = Hex.decodeHex(s.toCharArray());
        Binary b = (Binary) Binary.parse("0x" + s);
        assertArrayEquals(b.getBytes(), result);
    }

    @Test
    public void canParseMultiByteHex() throws Exception {
        String s = "ABCD";
        byte[] result = Hex.decodeHex(s.toCharArray());
        Binary b = (Binary) Binary.parse("0x" + s);
        assertArrayEquals(b.getBytes(), result);
    }

    @Test
    public void canConvertToHexString() {
        byte[] data = { (byte) 0x1F, (byte) 0xAB };
        String result = "0x" + Hex.encodeHexString(data);
        Binary b = new Binary(data);
        assertEquals(result, b.toString());
    }

    @Test
    public void getLengthTest() throws Exception {
        String s = "ABCDEF";
        Binary b = (Binary) Binary.parse("0x" + s);
        assertEquals(b.getLength(), 3);
    }

    @Test
    public void equalToTest() throws Exception {
        String s = "0xABCDEF";
        Binary b1 = (Binary) Binary.parse(s);
        Binary b2 = (Binary) Binary.parse(s);
        assertTrue(b1.equals(b2));
    }

    @Test
    public void notEqualToTest() throws Exception {
        String s = "BCDEF";
        Binary b1 = (Binary) Binary.parse("0xA" + s);
        Binary b2 = (Binary) Binary.parse("0xB" + s);
        assertFalse(b1.equals(b2));
    }
}
