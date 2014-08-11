package dbfit.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.Assert.*;

public class BigDecimalParseDelegateLocaleTest {
    @Test
    public void aDecimalOfLocaleFormatIsParsed() {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.getDefault());
        BigDecimalNormaliser bdn = new BigDecimalNormaliser();
        BigDecimal bd1 = (BigDecimal) bdn.normalise(new BigDecimal("111.111"));
        BigDecimal bd2 = (BigDecimal) bdn.normalise(BigDecimalParseDelegate.parse("111" + dfs.getDecimalSeparator() + "111"));
        assertEquals(bd1, bd2);
    }

}
