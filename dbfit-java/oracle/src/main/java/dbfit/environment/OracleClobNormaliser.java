package dbfit.environment;

import java.sql.SQLException;

import dbfit.util.TypeTransformer;

public class OracleClobNormaliser implements TypeTransformer {

    private static final int MAX_CLOB_LENGTH = 10000;

    @Override
    public Object transform(Object o) throws SQLException {
        if (o == null)
            return null;
        if (!(o instanceof java.sql.Clob)) {
            throw new UnsupportedOperationException("OracleClobNormaliser cannot work with " + o.getClass());
        }
        java.sql.Clob clob = (java.sql.Clob) o;
        if (clob.length() > MAX_CLOB_LENGTH)
            throw new UnsupportedOperationException(
                    "Clobs larger than " + MAX_CLOB_LENGTH + " bytes are not supported by DBFIT");
        return clob.getSubString(1, MAX_CLOB_LENGTH);
    }

}
