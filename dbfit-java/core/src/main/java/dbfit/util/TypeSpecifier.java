package dbfit.util;

import java.sql.SQLException;

public interface TypeSpecifier {
    public Object specify(Object o) throws SQLException ;
}
