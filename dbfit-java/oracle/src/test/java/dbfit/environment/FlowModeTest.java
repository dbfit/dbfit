package dbfit.environment;

import fitnesse.junit.FitNesseSuite;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseSuite.*;

@RunWith(FitNesseSuite.class)
@Name("DbFit.AcceptanceTests.JavaTests.OracleTests.FlowMode")
@FitnesseDir("../..")
@OutputDir("../../tmp")
@Port(1234)
public class FlowModeTest {
    @Test public void dummy(){}
}