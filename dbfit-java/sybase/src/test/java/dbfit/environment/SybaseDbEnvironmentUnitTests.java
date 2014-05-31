package dbfit.environment;

import dbfit.api.DBEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.environment.SybaseEnvironment;
import dbfit.util.Direction;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

public class SybaseDbEnvironmentUnitTests {

    @Test
    public void buildInsertCommand_AllInputParameters() throws Exception {
        dbfit.environment.SybaseEnvironment env = new SybaseEnvironment("Sybase");

        String expectedResult = "insert into DummyTable([Column1],[Column Two],[ColumnThree]) values (?,?,?)";
        DbParameterAccessor[] parameters = new DbParameterAccessor[3];

        parameters[0] = new DbParameterAccessor("Column1", Direction.INPUT, 0, null, 0);
        parameters[1] = new DbParameterAccessor("Column Two", Direction.INPUT, 0, null, 1);
        parameters[2] = new DbParameterAccessor("ColumnThree", Direction.INPUT, 0, null, 2);

        String actualResult = env.buildInsertCommand("DummyTable", parameters);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void buildInsertCommand_SpiceItUpWithAnOutputParameter() throws Exception {
        dbfit.environment.SybaseEnvironment env = new SybaseEnvironment("Sybase");

        String expectedResult = "insert into DummyTable([Column1]) values (?)";
        DbParameterAccessor[] parameters = new DbParameterAccessor[2];

        parameters[0] = new DbParameterAccessor("Column1", Direction.INPUT, 0, null, 0);
        parameters[1] = new DbParameterAccessor("Column2", Direction.OUTPUT, 0, null, 0);

        String actualResult = env.buildInsertCommand("DummyTable", parameters);

        assertEquals(expectedResult, actualResult);
    }
}

