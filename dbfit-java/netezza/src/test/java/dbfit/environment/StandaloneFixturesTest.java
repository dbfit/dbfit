package dbfit.environment;

import fitnesse.junit.FitNesseSuite;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseSuite.*;

@RunWith(FitNesseSuite.class)
@Name("DbFit.AcceptanceTests.JavaTests.NetezzaTests.StandaloneFixtures")
@FitnesseDir("../..")
@OutputDir(systemProperty = "java.io.tmpdir", pathExtension = "fitnesse")
public class StandaloneFixturesTest {
    @Test public void dummy(){}
}
