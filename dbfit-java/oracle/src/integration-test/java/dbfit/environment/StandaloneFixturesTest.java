package dbfit.environment;

import fitnesse.junit.FitNesseRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseRunner.*;

@RunWith(FitNesseRunner.class)
@Suite("DbFit.AcceptanceTests.JavaTests.OracleTests.StandaloneFixtures")
@FitnesseDir("../..")
@OutputDir(systemProperty = "java.io.tmpdir", pathExtension = "fitnesse")
public class StandaloneFixturesTest {
    @Test public void dummy(){}
}
