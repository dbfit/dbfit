package dbfit.environment;

import java.sql.SQLException;

import dbfit.util.TypeNormaliser;

public class SqlDateNormaliser implements TypeNormaliser {

    @Override
    public Object normalise(Object o) throws SQLException {
        if (o == null)
            return null;
        if (!(o instanceof java.sql.Date)) {
            throw new UnsupportedOperationException(
                    "SqlDateNormaliser cannot work with " + o.getClass());
        }
        java.sql.Date ts = (java.sql.Date) o;
        return new java.sql.Timestamp(ts.getTime());
    }

}
