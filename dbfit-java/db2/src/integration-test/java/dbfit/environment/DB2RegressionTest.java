package dbfit.environment;

import fitnesse.junit.FitNesseRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseRunner.*;

public class DB2RegressionTest {

    @RunWith(FitNesseRunner.class)
    @Suite("DbFit.AcceptanceTests.JavaTests.Db2Tests.Db2Tests.FlowMode")
    @FitnesseDir("../..")
    @OutputDir("../../tmp")
    public static class FlowModeTest {
        @Test public void dummy(){}
    }

}
