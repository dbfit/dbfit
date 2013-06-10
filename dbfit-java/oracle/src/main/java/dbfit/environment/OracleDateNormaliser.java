package dbfit.environment;

import java.sql.SQLException;

import dbfit.util.TypeNormaliser;

public class OracleDateNormaliser implements TypeNormaliser {

    @Override
    public Object normalise(Object o) throws SQLException {
        if (o == null)
            return null;
        if (!(o instanceof oracle.sql.DATE)) {
            throw new UnsupportedOperationException(
                    "OracleDateNormaliser cannot work with " + o.getClass());
        }
        oracle.sql.DATE ts = (oracle.sql.DATE) o;
        return ts.timestampValue();
    }

}
