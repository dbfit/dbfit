package dbfit.util;

import java.math.BigDecimal;

public class BigDecimalNormaliser implements TypeTransformer {

    @Override
    public Object transform(final Object o) {
        return (o == null) ? null : new NormalisedBigDecimal((BigDecimal) o);
    }
}
