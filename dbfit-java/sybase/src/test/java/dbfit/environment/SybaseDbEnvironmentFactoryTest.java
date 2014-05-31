package dbfit.environment;

import dbfit.api.DBEnvironment;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

public class SybaseDbEnvironmentFactoryTest {

    @Ignore("Sybase driver not available during build")
    @Test
    public void newDbEnvironmentTest() throws Exception {
        DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Sybase");
        assertNotNull(env);
    }
}

