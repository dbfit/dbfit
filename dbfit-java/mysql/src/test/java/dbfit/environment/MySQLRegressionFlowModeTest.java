package dbfit.environment;

import org.junit.Test;
import org.junit.runner.RunWith;

import fitnesse.trinidad.FitnesseSuite;
import fitnesse.trinidad.FitnesseSuite.*;

@RunWith(FitnesseSuite.class)
@Name("AcceptanceTests.JavaTests.MySqlTests.FlowMode")
@FitnesseDir("../../")
@OutputDir(systemProperty = "java.io.tmpdir", pathExtension = "fitnesse")
public class MySQLRegressionFlowModeTest {
  @Test
  public void dummy(){
  
  }
}
