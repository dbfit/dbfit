package dbfit.util;

import java.math.BigDecimal;

public class BigDecimalParseDelegate {
    public static Object parse(String s) {
        return new NormalisedBigDecimal(new BigDecimal(s));
    }
}
