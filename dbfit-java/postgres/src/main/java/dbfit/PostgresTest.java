package dbfit;

public class PostgresTest extends DatabaseTest {
    public PostgresTest(){
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Postgres"));
    }
}

