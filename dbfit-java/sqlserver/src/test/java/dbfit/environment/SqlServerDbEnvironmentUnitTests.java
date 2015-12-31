package dbfit.environment;

import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.TypeTransformerFactory;

import org.junit.Test;
import static org.junit.Assert.*;

public class SqlServerDbEnvironmentUnitTests {

    private TypeTransformerFactory dbfitToJdbcTransformerFactory = new TypeTransformerFactory();

    @Test
    public void buildInsertCommand_AllInputParameters() throws Exception {
        SqlServerEnvironment env = new SqlServerEnvironment("SqlServer");

        String expectedResult = "insert into DummyTable([Column1],[Column Two],[ColumnThree]) values (?,?,?)";
        DbParameterAccessor[] parameters = new DbParameterAccessor[3];

        parameters[0] = new DbParameterAccessor("Column1", Direction.INPUT, 0, null, 0, dbfitToJdbcTransformerFactory);
        parameters[1] = new DbParameterAccessor("Column Two", Direction.INPUT, 0, null, 1, dbfitToJdbcTransformerFactory);
        parameters[2] = new DbParameterAccessor("ColumnThree", Direction.INPUT, 0, null, 2, dbfitToJdbcTransformerFactory);

        String actualResult = env.buildInsertCommand("DummyTable", parameters);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void buildInsertCommand_SpiceItUpWithAnOutputParameter() throws Exception {
        SqlServerEnvironment env = new SqlServerEnvironment("SqlServer");

        String expectedResult = "insert into DummyTable([Column1]) values (?)";
        DbParameterAccessor[] parameters = new DbParameterAccessor[2];

        parameters[0] = new DbParameterAccessor("Column1", Direction.INPUT, 0, null, 0, dbfitToJdbcTransformerFactory);
        parameters[1] = new DbParameterAccessor("Column2", Direction.OUTPUT, 0, null, 0, dbfitToJdbcTransformerFactory);

        String actualResult = env.buildInsertCommand("DummyTable", parameters);

        assertEquals(expectedResult, actualResult);
    }
}

