package dbfit.fixture;

import java.sql.*;

public class StatementExecution {
    private Savepoint savepoint;
    private PreparedStatement statement;

    public StatementExecution(PreparedStatement statement) {
        this(statement, true);
    }

    public StatementExecution(PreparedStatement statement, boolean clearParameters) {
        this.statement = statement;
        if (clearParameters) {
            try {
                statement.clearParameters();
            } catch (SQLException e) {
                throw new RuntimeException("Exception while clearing parameters on PreparedStatement", e);
            }
        }
    }

    public void createSavepoint() {
        String savepointName = "eee" + this.hashCode();
        if (savepointName.length() > 10) savepointName = savepointName.substring(1, 9);
        savepoint = null;

        try {
            savepoint = statement.getConnection().setSavepoint(savepointName);
        } catch (SQLException e) {
            throw new RuntimeException("Exception while setting savepoint", e);
        }
    }

    public void restoreSavepoint() {
        if (savepoint != null) {
            try {
                statement.getConnection().rollback(savepoint);
            } catch (SQLException e) {
                throw new RuntimeException("Exception while restoring savepoint", e);
            }
        }
    }

    public void run() throws SQLException {
        statement.execute();
    }

    public void registerOutParameter(int index, int sqlType) throws SQLException {
        convertStatementToCallable().registerOutParameter(index, sqlType);
    }

    public void setObject(int index, Object value) throws SQLException {
        statement.setObject(index, value);
    }

    public Object getObject(int index) throws SQLException {
        return convertStatementToCallable().getObject(index);
    }

    //really ugly, but a hack to support mysql, because it will not execute inserts with a callable statement
    private CallableStatement convertStatementToCallable() throws SQLException {
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
}
