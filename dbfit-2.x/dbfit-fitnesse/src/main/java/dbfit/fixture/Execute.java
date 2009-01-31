package dbfit.fixture;
import java.sql.SQLException;

import org.dbfit.core.DBEnvironment;
import org.dbfit.core.DbEnvironmentFactory;
import org.dbfit.core.DbObject;
import org.dbfit.core.DbStatement;

import dbfit.util.FitNesseTestHost;

public class Execute extends DbObjectExecutionFixture{
	private String statementText;
	private DBEnvironment dbEnvironment;

	public Execute (){
		dbEnvironment=DbEnvironmentFactory.getDefaultEnvironment();
	}
	public Execute (DBEnvironment env, String statement){
		this.statementText=statement;
		this.dbEnvironment=env;
	}
	protected DbObject getTargetDbObject() throws SQLException {
		if (statementText==null) statementText=args[0];
		return new DbStatement(dbEnvironment,statementText, FitNesseTestHost.getInstance());
	}	
}
