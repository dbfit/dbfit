package dbfit.environment;

import dbfit.api.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

public class DbEnvironmentFactoryTest {
    private static final String DB_ENVIRONMENT_NAME = "NonexistentDbEnvironment";
    private final DbEnvironmentFactory factory = DbEnvironmentFactory.newFactoryInstance();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void prepare() {
        factory.unregisterEnv(DB_ENVIRONMENT_NAME);
    }

    @Test
    public void unsupportedEnvironmentInstantiationShouldRaiseException() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("DB Environment not supported:" + DB_ENVIRONMENT_NAME);

        factory.createEnvironmentInstance(DB_ENVIRONMENT_NAME);
    }
}

