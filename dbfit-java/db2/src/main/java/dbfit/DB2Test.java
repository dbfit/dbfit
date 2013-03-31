package dbfit;

public class DB2Test  extends DatabaseTest {
    public DB2Test(){
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("DB2"));
    }
}

