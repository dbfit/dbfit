package dbfit;

public class HSQLDBTest extends DatabaseTest {
    public HSQLDBTest() {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("HSQLDB"));
    }
}

