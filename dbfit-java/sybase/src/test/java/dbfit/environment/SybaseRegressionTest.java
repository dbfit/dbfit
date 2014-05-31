package dbfit.environment;

import fitnesse.junit.FitNesseSuite;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseSuite.*;

public class SybaseRegressionTest {
    @RunWith(FitNesseSuite.class)
    @Name("DbFit.AcceptanceTests.JavaTests.SybaseTests.FlowMode")
    @FitnesseDir("../..")
    @OutputDir("../../tmp")
    @Ignore
    public static class FlowModeTest {
        @Test public void dummy(){}
    }

    @RunWith(FitNesseSuite.class)
    @Name("DbFit.AcceptanceTests.JavaTests.SybaseTests.StandaloneFixtures")
    @FitnesseDir("../..")
    @OutputDir("../../tmp")
    @Ignore
    public static class StandaloneFixturesTest {
        @Test public void dummy(){}
    }
}
