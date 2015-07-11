package dbfit.util;

import java.math.BigInteger;

public class BigIntegerParseDelegate {
    public static Object parse(String s) {
        return new BigInteger(s);
    }
}
