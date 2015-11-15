package dbfit.environment;

import java.sql.SQLException;

import dbfit.util.TypeTransformer;

public class OracleTimestampNormaliser implements TypeTransformer {

    @Override
    public Object transform(Object o) throws SQLException {
        if (o == null)
            return null;
        if (!(o instanceof oracle.sql.TIMESTAMP)) {
            throw new UnsupportedOperationException(
                    "OracleTimestampNormaliser cannot work with "
                            + o.getClass());
        }
        oracle.sql.TIMESTAMP ts = (oracle.sql.TIMESTAMP) o;
        return ts.timestampValue();
    }

}
