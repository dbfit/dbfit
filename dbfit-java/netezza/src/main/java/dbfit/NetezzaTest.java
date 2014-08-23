package dbfit;

import dbfit.environment.NetezzaEnvironment;

public class NetezzaTest extends DatabaseTest {
    public NetezzaTest() {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Netezza"));
    }
}

