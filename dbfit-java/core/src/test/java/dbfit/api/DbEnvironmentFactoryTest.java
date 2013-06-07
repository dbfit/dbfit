package dbfit.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DbEnvironmentFactoryTest {
    private static final String NE_DB_ENVIRONMENT_NAME = "NonexistentDbEnvironment";
    private static final String SOME_ENVIRONMENT_NAME = "SomeDbEnvironment";
    private static final String NE_DRIVER_CLASS_NAME = "non.existent.Db.Driver";

    private final DbEnvironmentFactory factory = DbEnvironmentFactory.newFactoryInstance();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void prepare() {
        factory.unregisterEnv(NE_DB_ENVIRONMENT_NAME);
        factory.registerEnv(SOME_ENVIRONMENT_NAME, NE_DRIVER_CLASS_NAME);
    }

    @Test
    public void unsupportedEnvironmentInstantiationShouldRaiseException() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("DB Environment not supported:" + NE_DB_ENVIRONMENT_NAME);

        factory.createEnvironmentInstance(NE_DB_ENVIRONMENT_NAME);
    }

    @Test
    public void newDbEnvironmentWithMissingDriverShouldRaiseSelfExplainingException() throws Exception {
        expectedEx.expectMessage("Cannot load " + SOME_ENVIRONMENT_NAME
                + " database driver " + NE_DRIVER_CLASS_NAME);

        factory.createEnvironmentInstance(SOME_ENVIRONMENT_NAME);
    }
}

