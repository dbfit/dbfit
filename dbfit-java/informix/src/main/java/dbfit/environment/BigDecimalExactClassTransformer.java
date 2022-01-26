package dbfit.environment;

import dbfit.util.TypeTransformer;

public class BigDecimalExactClassTransformer implements TypeTransformer {

    @Override
    public Object transform(Object o) {
        if (o == null) {
            return null;
        }
        if (!(o instanceof java.math.BigDecimal)) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() +
                    " cannot transform objects of type " + o.getClass().getName());
        }
        return new java.math.BigDecimal(((java.math.BigDecimal) o).toString());
    }
}
