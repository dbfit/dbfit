package dbfit.fixture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.dbfit.core.DBEnvironment;
import org.dbfit.core.DbEnvironmentFactory;
import org.dbfit.db2.DB2Environment;
import org.dbfit.derby.DerbyEnvironment;
import org.dbfit.mysql.MySqlEnvironment;
import org.dbfit.oracle.OracleEnvironment;
import org.dbfit.postgre.PostgresEnvironment;
import org.dbfit.sqlserver.SqlServerEnvironment;

import fit.Parse;

public class DatabaseEnvironment extends fitlibrary.SequenceFixture{
		public void dbfitDotFixtureDotDatabaseEnvironment() {
		// required by fitnesse release 20080812
		}		
        public void doTable(Parse table) {
        	if (args.length>0){	
        		//todo:refactor to reflection-based factory
        		DBEnvironment oe ;
        		String requestedEnv=args[0].toUpperCase().trim();
        		if ("ORACLE".equals(requestedEnv)){
                    oe= new OracleEnvironment();
        		}
        		else if ("MYSQL".equals(requestedEnv)){
                    oe= new MySqlEnvironment();
        		}
        		else if ("SQLSERVER".equals(requestedEnv)){
                    oe= new SqlServerEnvironment();
        		}
        		else if ("DB2".equals(requestedEnv)){
        			oe=new DB2Environment();
        		}
        		else if ("DERBY".equals(requestedEnv)){
        			oe=new DerbyEnvironment();
        		}
        		if ("POSTGRES".equals(requestedEnv)){
                    oe= new PostgresEnvironment();
        		}
        		else throw new UnsupportedOperationException("DB Environment not supported:"+args[0]);
                DbEnvironmentFactory.setDefaultEnvironment(oe);
//                setSystemUnderTest(oe);
        	}     
        	super.doTable(table);
        }
        // workaround for fitlibrary limitation with system under test & abstract classes
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
