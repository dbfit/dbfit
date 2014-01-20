package dbfit.environment;

import fitnesse.junit.FitNesseSuite;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseSuite.*;

public class DerbyRegressionTest {

    @RunWith(FitNesseSuite.class)
    @Name("DbFit.AcceptanceTests.JavaTests.DerbyTests.FlowMode")
    @FitnesseDir("../..")
    @OutputDir("../../tmp")
    @Port(1234)
    public static class FlowModeTest {
        @Test public void dummy(){}
    }

    @RunWith(FitNesseSuite.class)
    @Name("DbFit.AcceptanceTests.JavaTests.DerbyTests.StandaloneFixtures")
    @FitnesseDir("../..")
    @OutputDir("../../tmp")
    @Port(1234)
    public static class StandaloneFixturesTest {
        @Test public void dummy(){}
    }

}
