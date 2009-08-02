package dbfit.fixture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;

import fit.Parse;

public class DatabaseEnvironment extends fitlibrary.SequenceFixture{
		private static final String[] environments=new String[]
		{"Oracle", "MySql", "SqlServer", "DB2","Derby","Postgres", "HSQLDB"};
		private static String getEnvironmentClassName(String requestedEnv){
			for (String environment:environments){
				if (environment.equalsIgnoreCase(requestedEnv))
					return "dbfit.environment."+environment+"Environment";
			}
			throw new IllegalArgumentException("DB Environment not supported:"+requestedEnv);
		}
        public void doTable(Parse table) {
        	if (args.length>0){	
        		setDatabaseEnvironment(args[0]);
        	}
        	super.doTable(table);
        }
        public void setDatabaseEnvironment(String requestedEnv) { 
    		requestedEnv=requestedEnv.trim().toUpperCase();
    		try{
    			DBEnvironment oe=(DBEnvironment) Class.forName(getEnvironmentClassName(requestedEnv)).newInstance();
    			DbEnvironmentFactory.setDefaultEnvironment(oe);
    		}
    		catch (Exception e){
    			throw new Error(e);
    		}
        }
    	public void rollback() throws SQLException {
    		DbEnvironmentFactory.getDefaultEnvironment().rollback();
    	}
    	public void commit() throws SQLException {
    		DbEnvironmentFactory.getDefaultEnvironment().commit();
    	}
    	public void connect(String connectionString) throws SQLException{
    		DbEnvironmentFactory.getDefaultEnvironment().connect(connectionString);
    	}
    	public void close() throws SQLException{
    		DbEnvironmentFactory.getDefaultEnvironment().closeConnection();
    	}
    	public void connect(String dataSource, String username, String password, String database) throws SQLException{
    		DbEnvironmentFactory.getDefaultEnvironment().connect(dataSource,username,password,database);	
    	}        	
    	public void connect(String dataSource, String username, String password) throws SQLException{
    		DbEnvironmentFactory.getDefaultEnvironment().connect(dataSource,username,password);	
    	}
    	public void connectUsingFile(String file) throws IOException,SQLException,FileNotFoundException{
    		DbEnvironmentFactory.getDefaultEnvironment().connectUsingFile(file);	
    	}
    	public void setOption(String option, String value){
    		dbfit.util.Options.setOption(option, value);
    	}
}
