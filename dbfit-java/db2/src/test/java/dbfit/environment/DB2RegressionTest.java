package dbfit.environment;

import fitnesse.junit.FitNesseSuite;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseSuite.*;

public class DB2RegressionTest {

    @RunWith(FitNesseSuite.class)
    @Name("DbFit.AcceptanceTests.JavaTests.Db2Tests.Db2Tests.FlowMode")
    @FitnesseDir("../..")
    @OutputDir("../../tmp")
    public static class FlowModeTest {
        @Test public void dummy(){}
    }

}
