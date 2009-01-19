package org.dbfit.greenpepper.util;

import java.sql.SQLException;

import org.dbfit.core.DBEnvironment;

public class Statement {
	private DBEnvironment environment;
	private String statementText;
	public Statement(DBEnvironment environment, String statementText) {
		super();
		this.environment = environment;
		this.statementText = statementText;
	}	
	public void execute() throws SQLException{
		environment.createStatementWithBoundFixtureSymbols(GreenPepperTestHost.getInstance(), statementText).execute();
	}
}
