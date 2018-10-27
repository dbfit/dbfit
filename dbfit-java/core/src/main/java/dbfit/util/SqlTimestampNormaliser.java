package dbfit.util;

import java.sql.SQLException;
import java.sql.Timestamp;

public class SqlTimestampNormaliser implements TypeTransformer {

    @Override
    public Object transform(Object o) throws SQLException {
        if (o == null) {
            return null;
        }
        if (!(o instanceof Timestamp)) {
            throw new UnsupportedOperationException(
                    "SqlTimestampNormaliser cannot work with "
                            + o.getClass());
        }
        return Timestamp.valueOf(o.toString());
    }
}
