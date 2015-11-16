package dbfit.fixture;

import java.sql.*;
import java.util.Map;

public class StatementExecution implements AutoCloseable {
    protected PreparedStatement statement;
    protected int returnValueInd = -1;

    public StatementExecution(PreparedStatement statement, boolean clearParameters, Map<Class<?>, TypeSpecifier> ts) {
        this.statement = statement;
        this.typeSpecifiers = ts;
        if (clearParameters) {
            try {
                statement.clearParameters();
            } catch (SQLException e) {
                throw new RuntimeException("Exception while clearing parameters on PreparedStatement", e);
            }
        }
    }

    public void run() throws SQLException {
        statement.execute();
    }

    public void registerOutParameter(int index, int sqlType, boolean isReturnValue) throws SQLException {
        if (isReturnValue) {
            returnValueInd = index;
        }
        convertStatementToCallable().registerOutParameter(index, sqlType);
    }

    public void setObject(int index, Object value, int sqlType, String userDefinedTypeName) throws SQLException {
        if (value == null) {
            statement.setNull(index, sqlType, userDefinedTypeName);
        } else {
            Object newValue;
            TypeSpecifier ts = typeSpecifiers.get(value.getClass());
            if (ts != null) {
                newValue = ts.specify(value);
            } else {
                newValue = value;
            }
            // Don't use the variant that takes sqlType.
            // Derby (at least) assumes no decimal places for Types.DECIMAL and truncates the source data.
            statement.setObject(index, newValue);
        }
    }

    public Object getObject(int index) throws SQLException {
        return convertStatementToCallable().getObject(index);
    }

    //really ugly, but a hack to support mysql, because it will not execute inserts with a callable statement
    protected CallableStatement convertStatementToCallable() throws SQLException {
        if (statement instanceof CallableStatement) return (CallableStatement) statement;
        throw new SQLException("This operation requires a callable statement instead of "+ statement.getClass().getName());
    }

    public Object getGeneratedKey(Class<?> type) throws SQLException, IllegalAccessException {
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {//todo: first try to find by name (mysql does not support name-based return keys)
            Object value;
            if (type == Integer.class) {
                value = rs.getInt(1);
            } else if (type == Long.class) {
                value = rs.getLong(1);
            } else {
                value = rs.getObject(1);
            }
            return value;
        }
        throw new IllegalAccessException("statement has not generated any keys");
    }

    @Override
    public void close() throws SQLException {
        statement.close();
    }
}
