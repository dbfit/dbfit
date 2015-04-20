package dbfit.environment;

import fitnesse.junit.FitNesseSuite;

import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseSuite.*;

public class HSQLDBRegressionTest {

    @RunWith(FitNesseSuite.class)
    @Name("DbFit.AcceptanceTests.JavaTests.HsqlTests.FlowMode")
    @FitnesseDir("../..")
    @OutputDir("../../tmp")
    public static class FlowModeTest {
        @Test public void dummy(){}
    }

}
