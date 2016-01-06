package dbfit.environment;

import java.sql.*;

import dbfit.fixture.StatementExecution;

public class InformixFunctionStatementExecution extends StatementExecution {
    private Object returnValue = null;
    private int returnValueInd = -1;

    public InformixFunctionStatementExecution(PreparedStatement statement) {
        super(statement);
    }

    @Override
    public void run() throws SQLException {
        try (ResultSet rs = statement.executeQuery()) {
            rs.next();
            returnValue = rs.getObject(1);
        }
    }

    @Override
    public void registerOutParameter(int index, int sqlType, boolean isReturnValue) throws SQLException {
        if (isReturnValue) {
            returnValueInd = index;
        } else {
            convertStatementToCallable().registerOutParameter(getRealIndex(index), sqlType);
        }
    }

    @Override
    public void setObject(int index, Object value, int sqlType, String userDefinedTypeName) throws SQLException {
        super.setObject(getRealIndex(index), value, sqlType, userDefinedTypeName);
    }

    @Override
    public Object getObject(int index) throws SQLException {
        if (returnValueInd == index) {
            return returnValue;
        } else {
            return super.getObject(getRealIndex(index));
        }
    }

    private int getRealIndex(int index) {
        return index - 1; // Ignore the "?" for the return value
    }
}
