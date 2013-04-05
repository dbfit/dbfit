package dbfit.environment;

import dbfit.api.DBEnvironment;
import org.junit.Test;
import static org.junit.Assert.*;

public class DerbyDbEnvironmentFactoryTest {

    @Test
    public void newDbEnvironmentTest() throws Exception {
        DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Derby");
        assertNotNull(env);
    }

    @Test
    public void newEmbeddedDerbyDbEnvironmentTest() throws Exception {
        DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("EmbeddedDerby");
        assertNotNull(env);
    }
}

