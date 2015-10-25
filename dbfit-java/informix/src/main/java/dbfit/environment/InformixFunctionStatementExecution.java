package dbfit.environment;

import java.sql.*;

import dbfit.fixture.StatementExecution;

public class InformixFunctionStatementExecution extends StatementExecution {
    private ResultSet rs;

    public InformixFunctionStatementExecution(PreparedStatement statement, boolean clearParameters) {
        super(statement, clearParameters);
    }

    @Override
    public void run() throws SQLException {
        rs = statement.executeQuery();
    }

    @Override
    public void registerOutParameter(int index, int sqlType, boolean isReturnValue) throws SQLException {
        if (isReturnValue) {
            returnValueInd = index;
        }
        int realIndex = index - 1; // Ignore the "?" for the return value.
        if (!isReturnValue) {
            convertStatementToCallable().registerOutParameter(realIndex, sqlType);
        }
    }

    @Override
    public void setObject(int index, Object value, int sqlType, String userDefinedTypeName) throws SQLException {
        super.setObject(index - 1, value, sqlType, userDefinedTypeName); // Ignore the "?" for the return value.
    }

    @Override
    public Object getObject(int index) throws SQLException {
        int realIndex = index - 1; // Ignore the "?" for the return value.
        if (returnValueInd == index) {
            return getReturnValue();
        } else {
            return convertStatementToCallable().getObject(realIndex);
        }
    }

    private Object getReturnValue() throws SQLException {
        if (returnValueInd > -1) {
            rs.next();
            Object o = rs.getObject(1);
            rs.close();
            return o;
        } else {
            return getObject(returnValueInd);
        }
    }
}
