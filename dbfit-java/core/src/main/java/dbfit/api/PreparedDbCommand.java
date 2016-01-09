package dbfit.api;

import java.sql.SQLException;

/**
 * Represents a prepared database statement with ability to bind input parameters and
 * to register and access output ones including return values.
 */
public interface PreparedDbCommand extends DbCommand, DbQuery {

    public void registerOutParameter(int index, int sqlType, boolean isReturnValue) throws SQLException;

    public void setObject(int index, Object value, int sqlType, String userDefinedTypeName) throws SQLException;

    public Object getObject(int index) throws SQLException;

    public Object getGeneratedKey(Class<?> type) throws SQLException, IllegalAccessException;
}
