package dbfit;

public class OracleTest extends DatabaseTest {
    public OracleTest() {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Oracle"));
    }
}

