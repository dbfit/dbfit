package dbfit.environment;

import fitnesse.junit.FitNesseRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fitnesse.junit.FitNesseRunner.*;

@RunWith(FitNesseRunner.class)
@Suite("DbFit.AcceptanceTests.JavaTests.PostgresTests.FlowMode")
@FitnesseDir("../..")
@OutputDir("../../tmp")
public class FlowModeTest {
    @Test public void dummy(){}
}
