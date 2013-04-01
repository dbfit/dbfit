package dbfit;

import dbfit.environment.OracleEnvironment;

public class OracleTest extends DatabaseTest {
    public OracleTest() {
        super(new OracleEnvironment());
    }
}

