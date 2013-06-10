package dbfit.environment;

import java.sql.SQLException;

import dbfit.util.TypeNormaliser;

public class OracleClobNormaliser implements TypeNormaliser {

    private static final int MAX_CLOB_LENGTH = 10000;

    @Override
    public Object normalise(Object o) throws SQLException {
        if (o == null)
            return null;
        if (!(o instanceof oracle.sql.CLOB)) {
            throw new UnsupportedOperationException(
                    "OracleClobNormaliser cannot work with " + o.getClass());
        }
        oracle.sql.CLOB clob = (oracle.sql.CLOB) o;
        if (clob.length() > MAX_CLOB_LENGTH)
            throw new UnsupportedOperationException("Clobs larger than "
                    + MAX_CLOB_LENGTH + " bytes are not supported by DBFIT");
        char[] buffer = new char[MAX_CLOB_LENGTH];
        int total = clob.getChars(1, MAX_CLOB_LENGTH, buffer);
        return String.valueOf(buffer, 0, total);
    }

}
