package dbfit;


import dbfit.api.DBEnvironment;
import dbfit.environment.*;

/**
 * Provides support for testing Derby databases (also known as JavaDB).
 * 
 * @author P&aring;l Brattberg, pal.brattberg@acando.com
 */
public class DerbyTest extends DatabaseTest {

    public DerbyTest() {
        super(new DerbyEnvironment());
    }
    protected DerbyTest(DBEnvironment env) {
        super(env);
    }
}

