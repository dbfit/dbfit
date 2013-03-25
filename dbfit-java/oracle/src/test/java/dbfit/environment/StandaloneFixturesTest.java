package dbfit.environment;

import fitnesse.junit.FitNesseSuite;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseSuite.*;

@RunWith(FitNesseSuite.class)
@Name("DbFit.AcceptanceTests.JavaTests.OracleTests.StandaloneFixtures")
@FitnesseDir("../..")
@OutputDir(systemProperty = "java.io.tmpdir", pathExtension = "fitnesse")
@Port(1234)
public class StandaloneFixturesTest {
    @Test public void dummy(){}
}