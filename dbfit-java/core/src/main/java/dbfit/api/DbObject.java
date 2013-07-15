package dbfit.api;

import dbfit.fixture.StatementExecution;
import dbfit.util.DbParameterAccessor;

import java.sql.SQLException;

import dbfit.util.Direction;

public interface DbObject {
    public StatementExecution buildPreparedStatement(DbParameterAccessor accessors[]) throws SQLException ;
    public DbParameterAccessor getDbParameterAccessor(String paramName, Direction expectedDirection) throws SQLException;
    int getExceptionCode(SQLException e);
}
