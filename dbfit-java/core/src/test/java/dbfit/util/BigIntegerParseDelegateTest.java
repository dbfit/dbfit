package dbfit.util;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class BigIntegerParseDelegateTest {
    @Test
    public void anIntegerIsParsed() {
        assertEquals(BigInteger.valueOf(2001), BigIntegerParseDelegate.parse("2001"));
    }
}
