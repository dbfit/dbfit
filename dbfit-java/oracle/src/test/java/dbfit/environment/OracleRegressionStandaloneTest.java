package dbfit.environment;

import org.junit.Test;
import org.junit.runner.RunWith;

import fitnesse.trinidad.FitnesseSuite;
import fitnesse.trinidad.FitnesseSuite.*;

@RunWith(FitnesseSuite.class)
@Name("AcceptanceTests.JavaTests.OracleTests.StandaloneFixtures")
@FitnesseDir("../core/src/main/resources")
@OutputDir(systemProperty = "java.io.tmpdir", pathExtension = "fitnesse")
public class OracleRegressionStandaloneTest {
  @Test
  public void dummy(){
  
  }
}
