package dbfit.fixture;

import java.sql.PreparedStatement;

import org.dbfit.core.DBEnvironment;
import org.dbfit.core.DbEnvironmentFactory;
import org.dbfit.core.TestHost;
import org.dbfit.fitnesse.FitNesseTestHost;

import fit.Fixture;
import fit.Parse;

public class Execute extends Fixture{
	private String statement;
	private DBEnvironment dbEnvironment;

	public Execute (){
		dbEnvironment=DbEnvironmentFactory.getDefaultEnvironment();
	}
	public Execute (DBEnvironment env, String statement){
		this.statement=statement;
		this.dbEnvironment=env;
	}
	public void doRows(Parse rows) {
		try{
			if (statement==null) statement=args[0];
			PreparedStatement st=dbEnvironment.createStatementWithBoundFixtureSymbols(FitNesseTestHost.getInstance(),statement);
			st.execute();
		}
		catch (Exception e){
			throw new Error(e);
		}
	}
}
