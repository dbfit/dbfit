package dbfit.util;

import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;

public class Binary {
    private byte[] binaryData;

    public Binary(final byte[] byteStream) {
        binaryData = byteStream;
    }

    public int getLength() {
        return binaryData.length;
    }

    public byte[] getBytes() {
        return binaryData;
    }

    public static Object parse(String s) throws Exception {
        //List<Byte> bytes = new ArrayList<Byte>();
        if (!s.substring(0, 2).equals("0x")) {
            throw new NumberFormatException("Hexadecimal string does not start with 0x");
        }
        String input = s.substring(2);
        return new Binary(Hex.decodeHex(input.toCharArray()));
    }

    @Override
    public String toString() {
        return "0x" + Hex.encodeHexString(binaryData);
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null)
            return false;
        if (!(other instanceof Binary)) {
            return false;
        }
        return Arrays.equals(binaryData, ((Binary) other).getBytes());
    }
}
