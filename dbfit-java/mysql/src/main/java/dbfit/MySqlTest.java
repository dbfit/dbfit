package dbfit;

public class MySqlTest extends DatabaseTest {
    public MySqlTest() {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("MySql"));
    }
}

