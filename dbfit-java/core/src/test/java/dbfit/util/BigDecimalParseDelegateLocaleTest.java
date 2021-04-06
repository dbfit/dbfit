package dbfit.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.Assert.*;

public class BigDecimalParseDelegateLocaleTest {
    private char ds = new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator();
    private BigDecimalNormaliser normaliser = new BigDecimalNormaliser();

    @Test
    public void aDecimalOfLocaleFormatIsParsed() {
        BigDecimal bd1 = normalise(new BigDecimal("111.111"));
        BigDecimal bd2 = normalise(BigDecimalParseDelegate.parse("111" + ds + "111"));
        assertEquals(bd1, bd2);
    }

    @Test
    public void aHugeDecimalOfLocaleFormatIsParsed() {
        BigDecimal bd1 = normalise(new BigDecimal("123456789012345.123456789012345"));
        BigDecimal bd2 = normalise(BigDecimalParseDelegate.parse("123456789012345" + ds + "123456789012345"));
        assertEquals(bd1, bd2);
    }

    @Test
    public void aNegativeDecimalOfLocaleFormatIsParsed() {
        BigDecimal bd1 = normalise(new BigDecimal("-98765.0000111"));
        BigDecimal bd2 = normalise(BigDecimalParseDelegate.parse("-98765" + ds + "0000111"));
        assertEquals(bd1, bd2);
    }

    private BigDecimal normalise(BigDecimal val) {
        return (BigDecimal) normaliser.normalise(val);
    }

    private BigDecimal normalise(Object val) {
        return (BigDecimal) normaliser.normalise(val);
    }

}
