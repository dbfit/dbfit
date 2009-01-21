package org.dbfit.greenpepper.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.dbfit.core.DBEnvironment;

import dbfit.util.DbParameterAccessor;

public class Statement implements DbObject {
	private DBEnvironment environment;
	private String statementText;
	public Statement(DBEnvironment environment, String statementText) {
		super();
		this.environment = environment;
		this.statementText = statementText;
	}	
	public PreparedStatement buildPreparedStatement(
			DbParameterAccessor[] accessors) throws SQLException {
		return environment.createStatementWithBoundFixtureSymbols(GreenPepperTestHost.getInstance(), statementText);
	}
	public DbParameterAccessor getDbParameterAccessor(String paramName, int expectedDirection){
		return null;
	}
	public DBEnvironment getDbEnvironment() {
		return environment;
	}
}
