package dbfit.environment;

import java.sql.SQLException;

import dbfit.util.TypeNormaliser;

public class InformixBigDecimalNormaliser implements TypeNormaliser {

    @Override
    public Object normalise(Object o) throws SQLException {
        if (o == null) {
            return null;
        }    
        if (!(o instanceof dbfit.util.NormalisedBigDecimal)) {
        	throw new UnsupportedOperationException("InformixBigDecimalNormaliser cannot work with " + o.getClass());
        }       
        java.math.BigDecimal bd = new java.math.BigDecimal(((java.math.BigDecimal) o).toString());
        return bd;
    }
}
