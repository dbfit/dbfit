package dbfit.fixture;

import java.sql.*;

public class StatementExecutionCapturingResultSetValue extends StatementExecution {
    private Object returnValue = null;
    private int returnValueInd = -1;
    private final int realIndexOffset;

    public StatementExecutionCapturingResultSetValue(PreparedStatement statement, int realIndexOffset) {
        super(statement);
        this.realIndexOffset = realIndexOffset;
    }

    public StatementExecutionCapturingResultSetValue(PreparedStatement statement) {
        // Ignore the "?" for the return value
        this(statement, -1);
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
        return index + realIndexOffset;
    }
}
