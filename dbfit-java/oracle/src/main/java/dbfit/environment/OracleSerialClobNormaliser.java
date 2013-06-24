package dbfit.environment;

import java.sql.SQLException;

import dbfit.util.TypeNormaliser;

public class OracleSerialClobNormaliser implements TypeNormaliser {

    private static final int MAX_CLOB_LENGTH = 10000;

    @Override
    public Object normalise(Object o) throws SQLException {
        if (o == null)
            return null;
        if (!(o instanceof oracle.jdbc.rowset.OracleSerialClob)) {
            throw new UnsupportedOperationException(
                    "OracleSerialClobNormaliser cannot work with " + o.getClass());
        }
        oracle.jdbc.rowset.OracleSerialClob clob = (oracle.jdbc.rowset.OracleSerialClob) o;
        if (clob.length() > MAX_CLOB_LENGTH)
            throw new UnsupportedOperationException("Clobs larger than "
                    + MAX_CLOB_LENGTH + " bytes are not supported by DBFIT");
        return clob.getSubString(1, (int)clob.length());
    }

}
