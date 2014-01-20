package dbfit.environment;

import fitnesse.junit.JUnitHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;

// Can't seem to convert this to use the FitNesseSuite runner
// since setUp and tearDown didn't seem to work
public class DerbyRegressionTest {

    private JUnitHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new JUnitHelper("../..", new File("../../tmp").getAbsolutePath());
        helper.setPort(1234);
    }

    @Test
    public void flowMode() throws Exception {
        helper.assertSuitePasses("DbFit.AcceptanceTests.JavaTests.DerbyTests.FlowMode");
    }

    @Test
    public void standaloneMode() throws Exception {
        helper.assertSuitePasses("DbFit.AcceptanceTests.JavaTests.DerbyTests.StandaloneFixtures");
    }

}
