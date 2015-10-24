package dbfit.fixture;

import java.sql.*;

public class StatementExecution implements AutoCloseable {
    private PreparedStatement statement;
    private boolean functionReturnValueViaResultSet;
    private boolean discountFunctionReturnValueParameter;
    private ResultSet rs;
    int returnValueInd = -1;

    public StatementExecution(PreparedStatement statement, boolean functionReturnValueViaResultSet, boolean discountFunctionReturnValueParameter) {
        this(statement, true, functionReturnValueViaResultSet, discountFunctionReturnValueParameter);
    }

    public StatementExecution(PreparedStatement statement, boolean clearParameters, boolean functionReturnValueViaResultSet, boolean discountFunctionReturnValueParameter) {
        this.statement = statement;
        if (clearParameters) {
            try {
                statement.clearParameters();
            } catch (SQLException e) {
                throw new RuntimeException("Exception while clearing parameters on PreparedStatement", e);
            }
        }
        this.functionReturnValueViaResultSet = functionReturnValueViaResultSet;
        this.discountFunctionReturnValueParameter = discountFunctionReturnValueParameter;
    }

    public void run() throws SQLException {
        if (functionReturnValueViaResultSet) {
            rs = statement.executeQuery();
        } else {
            statement.execute();
        }
    }

    public void registerOutParameter(int index, int sqlType, boolean isReturnValue) throws SQLException {
        if (isReturnValue) {
            returnValueInd = index;
        }
        int realIndex;
        if (functionReturnValueViaResultSet) {
            realIndex = index - 1; // Ignore the "?" for the return value.
        } else {
            realIndex = index;
        }
        if (!(isReturnValue && (discountFunctionReturnValueParameter || functionReturnValueViaResultSet))) {
            convertStatementToCallable().registerOutParameter(realIndex, sqlType);
        }
    }

    public void setObject(int index, Object value, int sqlType, String userDefinedTypeName) throws SQLException {
        int realIndex;
        if (functionReturnValueViaResultSet) {
            realIndex = index - 1; // Ignore the "?" for the return value.
        } else {
            realIndex = index;
        }
        if (value == null) {
            statement.setNull(realIndex, sqlType, userDefinedTypeName);
        } else {
            // Don't use the variant that takes sqlType.
            // Derby (at least) assumes no decimal places for Types.DECIMAL and truncates the source data.
            statement.setObject(realIndex, value);
        }
    }

    public Object getObject(int index) throws SQLException {
        int realIndex;
        if (functionReturnValueViaResultSet) {
            realIndex = index - 1; // Ignore the "?" for the return value.
        } else {
            realIndex = index;
        }
        if (functionReturnValueViaResultSet && returnValueInd == index) {
            return getReturnValue();
        } else {
            return convertStatementToCallable().getObject(realIndex);
        }
    }

    public Object getReturnValue() throws SQLException {
        if (functionReturnValueViaResultSet) {
            rs.next();
            Object o = rs.getObject(1);
            rs.close();
            return o;
        } else {
            return getObject(returnValueInd);
        }
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

    @Override
    public void close() throws SQLException {
        statement.close();
    }
}
