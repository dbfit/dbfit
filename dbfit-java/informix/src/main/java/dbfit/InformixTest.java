package dbfit;

import com.informix.jdbc.IfxSqliConnect;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;


/**
 * Created by muppana on 6/5/2014.
 */
public class InformixTest extends DatabaseTest {
    private IfxSqliConnect ifxSqliConnection;
    private static String Role;

    public InformixTest()  {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Informix"));
    }


   @Override
    public void connect(String dataSource, String username, String password, String database) throws SQLException {

        environment.connect(dataSource, username, password, database);
       System.out.println("database name="+database);
        environment.getConnection().setAutoCommit(false);

      // new StatementExecution.Savepoint((Connection) ifxSqliConnection.setSavepoint("test"));
    }

   @Override
    public void connect(String dataSource, String username, String password) throws SQLException {

        environment.connect(dataSource, username, password);
       System.out.println("dataSource name="+dataSource);
       if(dataSource.contains("sentryprod")){
           environment.getConnection().setAutoCommit(true);
           Role="dontdo";

       }else{
           environment.getConnection().setAutoCommit(false);
       }


      // new StatementExecution.Savepoint((Connection) ifxSqliConnection.setSavepoint("test"));
    }

@Override
    public void connect(String connectionString) throws SQLException {

        environment.connect(connectionString);
    if(connectionString.contains("sentryprod")){
        environment.getConnection().setAutoCommit(true);
        Role="dontdo";

    }else{
        environment.getConnection().setAutoCommit(false);
    }
   // new StatementExecution.Savepoint((Connection) ifxSqliConnection.setSavepoint("test"));
    }
@Override
    public void connectUsingFile(String filePath) throws SQLException, IOException, FileNotFoundException {
        environment.connectUsingFile(filePath);
        environment.getConnection().setAutoCommit(false);
    }
      // StatementExecution. Savepoint()
@Override
    public void rollback() throws SQLException {
        // System.out.println("Rolling back");
       if(Role.equals("dontdo")){
           environment.getConnection().setAutoCommit(false);
       }else {
           environment.rollback();
           environment.getConnection().setAutoCommit(false);
       }   }
}



