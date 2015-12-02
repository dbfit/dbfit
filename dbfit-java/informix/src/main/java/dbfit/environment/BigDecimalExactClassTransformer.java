package dbfit.environment;

import java.sql.SQLException;

import dbfit.util.TypeTransformer;

public class BigDecimalExactClassTransformer implements TypeTransformer {

    @Override
    public Object transform(Object o) throws SQLException {
        if (o == null) {
            return null;
        }
        if (!(o instanceof java.math.BigDecimal)) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() +
                    " cannot transform objects of type " + o.getClass().getName());
        }
        java.math.BigDecimal bd = new java.math.BigDecimal(((java.math.BigDecimal) o).toString());
        return bd;
    }
}
