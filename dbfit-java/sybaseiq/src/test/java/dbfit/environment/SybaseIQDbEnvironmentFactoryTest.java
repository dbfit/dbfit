package test.dbfit.environment;

import java.util.Map;

import org.junit.Test;

import dbfit.api.DBEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Log;
import static org.junit.Assert.*;

public class SybaseIQDbEnvironmentFactoryTest {

    @Test
    public void newDbEnvironmentTest() throws Exception {
        DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("MySql");
        assertNotNull(env);
    }
}

