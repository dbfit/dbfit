package dbfit.environment;

import dbfit.api.DBEnvironment;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

public class TeradataDbEnvironmentFactoryTest {

    @Ignore("Teradata tests not active on build yet")
    public void newDbEnvironmentTest() throws Exception {
        DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Teradata");
        assertNotNull(env);
    }
}

