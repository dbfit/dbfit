package org.dbfit.greenpepper.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.dbfit.core.DBEnvironment;

import com.greenpepper.interpreter.RuleForInterpreter;

import dbfit.util.DbParameterAccessor;

public class Statement implements DbObject {
	private DBEnvironment environment;
	private String statementText;
	public Statement(DBEnvironment environment, String statementText) {
		super();
		this.environment = environment;
		this.statementText = statementText;
	}	
	private static final Map<String,DbParameterAccessor> EMPTY=Collections.unmodifiableMap(new HashMap<String,DbParameterAccessor>());
	public PreparedStatement buildPreparedStatement(
			DbParameterAccessor[] accessors) throws SQLException {
		return environment.createStatementWithBoundFixtureSymbols(GreenPepperTestHost.getInstance(), statementText);
	}
//	public Map<String, DbParameterAccessor> getAllParams() {
//		return EMPTY;
//	}
	public DbParameterAccessor getDbParameterAccessor(String paramName, int expectedDirection){
		return null;
	}
	public DBEnvironment getDbEnvironment() {
		return environment;
	}
}
