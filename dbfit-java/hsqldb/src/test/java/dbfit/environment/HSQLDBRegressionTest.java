package dbfit.environment;

import fitnesse.junit.FitNesseRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseRunner.*;

public class HSQLDBRegressionTest {

    @RunWith(FitNesseRunner.class)
    @Suite("DbFit.AcceptanceTests.JavaTests.HsqlTests.FlowMode")
    @FitnesseDir("../..")
    @OutputDir("../../tmp")
    public static class FlowModeTest {
        @Test public void dummy(){}
    }

}
