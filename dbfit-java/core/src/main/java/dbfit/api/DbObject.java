package dbfit.api;

import dbfit.fixture.StatementExecution;
import dbfit.util.Direction;
import dbfit.util.ParameterOrColumn;

import java.sql.SQLException;

public interface DbObject {
    public StatementExecution buildPreparedStatement(ParameterOrColumn accessors[]) throws SQLException ;
    public ParameterOrColumn getDbParameterAccessor(String paramName, Direction expectedDirection) throws SQLException;
    int getExceptionCode(SQLException e);
}
