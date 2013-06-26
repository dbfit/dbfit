package dbfit.environment;

import dbfit.api.DBEnvironment;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

public class TeradataDbEnvironmentFactoryTest {

    @Ignore("Teradata driver not pulled on build yet")
    @Test
    public void newDbEnvironmentTest() throws Exception {
        DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Teradata");
        assertNotNull(env);
    }
}

