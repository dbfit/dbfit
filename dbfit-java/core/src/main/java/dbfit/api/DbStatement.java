package dbfit.api;

import dbfit.util.DbParameterAccessor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static dbfit.util.DbParameterAccessor.Direction;

public class DbStatement implements DbObject {
	private DBEnvironment environment;
	private String statementText;
	private TestHost testHost;
	public DbStatement() {
		environment=DbEnvironmentFactory.getDefaultEnvironment();
	}
	public DbStatement(DBEnvironment environment, String statementText, TestHost testHost) {
		this.environment = environment;
		this.statementText = statementText;
		this.testHost=testHost;
	}	
	public PreparedStatement buildPreparedStatement(
			DbParameterAccessor[] accessors) throws SQLException {
		return environment.createStatementWithBoundFixtureSymbols(testHost, statementText);
	}
	public DbParameterAccessor getDbParameterAccessor(String paramName, Direction expectedDirection){
		return null;
	}
	public DBEnvironment getDbEnvironment() {
		return environment;
	}
}
