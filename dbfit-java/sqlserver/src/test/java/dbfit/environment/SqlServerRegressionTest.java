package dbfit.environment;

import fitnesse.junit.JUnitHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

@Ignore
public class SqlServerRegressionTest {

    private JUnitHelper helper;

    @Before
    public void setUp() {
        helper = new JUnitHelper("../..", new File("../../tmp").getAbsolutePath());
        helper.setPort(1234);
    }

    @Test
    public void flowMode() throws Exception {
        helper.assertSuitePasses("AcceptanceTests.JavaTests.SqlServerTests.FlowMode");
    }

    @Test
    public void standaloneMode() throws Exception {
        helper.assertSuitePasses("AcceptanceTests.JavaTests.SqlServerTests.StandaloneFixtures");
    }
}