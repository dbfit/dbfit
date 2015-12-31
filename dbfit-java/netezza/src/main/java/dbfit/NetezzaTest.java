package dbfit;

public class NetezzaTest extends DatabaseTest {
    public NetezzaTest() {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Netezza"));
    }
}

