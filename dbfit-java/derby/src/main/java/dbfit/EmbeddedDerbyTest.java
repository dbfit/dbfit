package dbfit;

import dbfit.api.DbEnvironmentFactory;

/**
 * Provides support for testing Derby databases in embedded mode
 *
 */
public class EmbeddedDerbyTest extends DatabaseTest {

    public EmbeddedDerbyTest() {
        super(DbEnvironmentFactory.newEnvironmentInstance("EmbeddedDerby"));
    }
}

