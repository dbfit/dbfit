package dbfit.api;

import dbfit.util.DbParameterAccessor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static dbfit.util.DbParameterAccessor.Direction;

public interface DbObject {
	public DBEnvironment getDbEnvironment();
	public PreparedStatement buildPreparedStatement(DbParameterAccessor accessors[]) throws SQLException ;
	public DbParameterAccessor getDbParameterAccessor(String paramName, Direction expectedDirection) throws SQLException;
}
