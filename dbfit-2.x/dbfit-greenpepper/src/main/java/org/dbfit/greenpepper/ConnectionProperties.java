package org.dbfit.greenpepper;

import java.sql.SQLException;
import org.dbfit.core.DBEnvironment;

public class ConnectionProperties {
	public DBEnvironment dbEnvironment;
	String database;
	String host;
	String username;
	String password;
	
	public ConnectionProperties(DBEnvironment environment, String... params){
		this.dbEnvironment=environment;
		host=params[0];
		username=params[1];
		password=params[2];
		if (params.length==4){
			database=params[3];
		}			
	}
	public void connect() throws SQLException{
		System.err.println ("Connecting to "+host);
		dbEnvironment.connect(host, username, password, database);
		dbEnvironment.getConnection().setAutoCommit(false);		
	}	
}
