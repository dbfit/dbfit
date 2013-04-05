package dbfit.environment;

import dbfit.api.DBEnvironment;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

public class HSQLDBEnvironmentFactoryTest {

    @Ignore("HSQLDB driver not available during build")
    @Test
    public void newDbEnvironmentTest() throws Exception {
        DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("HSQLDB");
        assertNotNull(env);
    }
}

