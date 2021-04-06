package dbfit.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

public class BigDecimalParseDelegate {
    private static DecimalFormat nf = new DecimalFormat();

    public static Object parse(String s) {
        try {
            nf.setParseBigDecimal(true);
            return new NormalisedBigDecimal(new BigDecimal(nf.parse(s).toString()));
        } catch (ParseException e) {
            return new NormalisedBigDecimal(new BigDecimal(s));
        }
    }
}
