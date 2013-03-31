package dbfit;

public class SqlServerTest extends DatabaseTest {
    public SqlServerTest(){
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("SqlServer"));
    }
}

