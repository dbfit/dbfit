package dbfit.environment;

import java.sql.SQLException;

import dbfit.util.TypeTransformer;

public class InformixBigDecimalSpecifier implements TypeTransformer {

    @Override
    public Object transform(Object o) throws SQLException {
        if (o == null) {
            return null;
        }
        if (!(o instanceof dbfit.util.NormalisedBigDecimal)) {
            throw new UnsupportedOperationException("InformixBigDecimalSpecifier cannot work with " + o.getClass());
        }
        java.math.BigDecimal bd = new java.math.BigDecimal(((java.math.BigDecimal) o).toString());
        return bd;
    }
}
