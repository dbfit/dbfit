package dbfit.environment;

import dbfit.api.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

public class OracleDbEnvironmentFactoryTest {
    private DbEnvironmentFactory factory = DbEnvironmentFactory.newFactoryInstance();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Ignore("Implementation not ready yet")
    @Test
    public void newDbEnvironmentWithMissingDriverShouldRaiseSelfExplainingException() throws Exception {
        String driverClassName = "non.existent.Db.Driver";
        String environmentName = "Oracle";

        factory.registerEnv(environmentName, driverClassName);

        expectedEx.expectMessage("Cannot load " + environmentName
                + " database driver " + driverClassName);

        DBEnvironment env = factory.createEnvironmentInstance(environmentName);
    }

    @Test
    public void newOracleDbEnvironmentTest() throws Exception {
        String driverClassName = "oracle.jdbc.OracleDriver";
        String environmentName = "Oracle";
        
        factory.registerEnv(environmentName, driverClassName);

        DBEnvironment env = factory.createEnvironmentInstance(environmentName);

        assertNotNull(env);
    }
}

