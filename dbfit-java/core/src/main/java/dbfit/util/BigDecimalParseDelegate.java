package dbfit.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

public class BigDecimalParseDelegate {
    private static NumberFormat nf = NumberFormat.getInstance();
    public static Object parse(String s) {
        try {
            return new NormalisedBigDecimal(new BigDecimal(nf.parse(s).toString()));
        } catch (ParseException e) {
            return new NormalisedBigDecimal(new BigDecimal(s));
        }
    }
}
