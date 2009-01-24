package org.dbfit.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;


import dbfit.util.DbParameterAccessor;

public class DbStatement implements DbObject {
	private DBEnvironment environment;
	private String statementText;
	private TestHost testHost;
	public DbStatement(DBEnvironment environment, String statementText, TestHost testHost) {
		this.environment = environment;
		this.statementText = statementText;
		this.testHost=testHost;
	}	
	public PreparedStatement buildPreparedStatement(
			DbParameterAccessor[] accessors) throws SQLException {
		return environment.createStatementWithBoundFixtureSymbols(testHost, statementText);
	}
	public DbParameterAccessor getDbParameterAccessor(String paramName, int expectedDirection){
		return null;
	}
}
