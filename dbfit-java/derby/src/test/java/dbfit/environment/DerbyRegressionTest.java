package dbfit.environment;

import fitnesse.junit.JUnitHelper;
import org.apache.derby.drda.NetworkServerControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;

// Can't seem to convert this to use the FitNesseSuite runner
// since setUp and tearDown didn't seem to work
public class DerbyRegressionTest {

    private JUnitHelper helper;
    private NetworkServerControl serverControl;

    @Before
    public void setUp() throws Exception {
        helper = new JUnitHelper("../..", new File("../../tmp").getAbsolutePath());
        helper.setPort(1234);

        serverControl = new NetworkServerControl();
        serverControl.start(new PrintWriter(System.out, true));
    }

    @Test
    public void flowMode() throws Exception {
        helper.assertSuitePasses("DbFit.AcceptanceTests.JavaTests.DerbyTests.FlowMode");
    }

    @After
    public void tearDown() throws Exception {
        serverControl.shutdown();
    }
}
