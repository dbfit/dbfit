package dbfit.util;

import java.math.BigDecimal;

public class BigDecimalNormaliser implements TypeNormaliser {

    @Override
    public Object normalise(final Object o) {
        return (o == null) ? null : new NormalisedBigDecimal((BigDecimal) o);
    }
}
