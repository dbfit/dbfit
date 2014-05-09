package dbfit;


import dbfit.api.DBEnvironment;

/**
 * Provides support for testing Derby databases (also known as JavaDB).
 * 
 * @author P&aring;l Brattberg, pal.brattberg@acando.com
 */
public class DerbyTest extends DatabaseTest {

    public DerbyTest() {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Derby"));
    }
    protected DerbyTest(DBEnvironment env) {
        super(env);
    }
}

