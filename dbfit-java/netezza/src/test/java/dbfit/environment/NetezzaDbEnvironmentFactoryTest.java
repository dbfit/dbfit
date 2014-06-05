package dbfit.environment;

import dbfit.api.DBEnvironment;
import org.junit.Test;
import static org.junit.Assert.*;

public class NetezzaDbEnvironmentFactoryTest {

    @Test
    public void newDbEnvironmentTest() throws Exception {
        DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Netezza");
        assertNotNull(env);
    }
}

