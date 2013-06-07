package dbfit;

public class SybaseIQTest extends DatabaseTest {
    public SybaseIQTest() {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("SybaseIQ"));
    }
}
