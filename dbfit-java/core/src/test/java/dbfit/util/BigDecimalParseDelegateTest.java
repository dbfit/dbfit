package dbfit.util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class BigDecimalParseDelegateTest {
    @Test
    public void anIntegerIsParsed() {
        assertEquals(new BigDecimal(2001), BigDecimalParseDelegate.parse("2001"));
    }
}
