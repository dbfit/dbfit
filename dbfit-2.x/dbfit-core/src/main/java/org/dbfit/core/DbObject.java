package org.dbfit.core;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import dbfit.util.DbParameterAccessor;

public interface DbObject {
	public DBEnvironment getDbEnvironment();
	public PreparedStatement buildPreparedStatement(DbParameterAccessor accessors[]) throws SQLException ;
	public DbParameterAccessor getDbParameterAccessor(String paramName, int expectedDirection) throws SQLException;
}
