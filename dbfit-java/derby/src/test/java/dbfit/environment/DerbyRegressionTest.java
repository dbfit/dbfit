package dbfit.environment;

import fitnesse.junit.JUnitHelper;
import org.apache.derby.drda.NetworkServerControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;

public class DerbyRegressionTest {

    private JUnitHelper helper;
    private NetworkServerControl serverControl;

    @Before
    public void setUp() throws Exception {
        helper = new JUnitHelper("../..", new File(System.getProperty("java.io.tmpdir"),"fitnesse").getAbsolutePath());
        helper.setPort(1234);

        serverControl = new NetworkServerControl();
        serverControl.start(new PrintWriter(System.out, true));
    }

    @Test
    public void flowMode() throws Exception {
        helper.assertSuitePasses("AcceptanceTests.JavaTests.DerbyTests.FlowMode");
    }

    @After
    public void tearDown() throws Exception {
        serverControl.shutdown();
    }
}
