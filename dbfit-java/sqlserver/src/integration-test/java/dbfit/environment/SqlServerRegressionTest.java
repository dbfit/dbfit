package dbfit.environment;

import fitnesse.junit.FitNesseRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseRunner.*;

public class SqlServerRegressionTest {
    @RunWith(FitNesseRunner.class)
    @Suite("DbFit.AcceptanceTests.JavaTests.SqlServerTests.FlowMode")
    @FitnesseDir("../..")
    @OutputDir("../../tmp")
    public static class FlowModeTest {
        @Test
        public void dummy() {
        }
    }

    @RunWith(FitNesseRunner.class)
    @Suite("DbFit.AcceptanceTests.JavaTests.SqlServerTests.StandaloneFixtures")
    @FitnesseDir("../..")
    @OutputDir("../../tmp")
    public static class StandaloneFixturesTest {
        @Test
        public void dummy() {
        }
    }
}
