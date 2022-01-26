package dbfit.util;

import java.sql.SQLException;

public interface TypeTransformer {
    public Object transform(Object o) throws SQLException;
}
