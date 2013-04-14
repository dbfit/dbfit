package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

public class OracleBooleanSpCommandTest {

    private SpGeneratorOutput output;
    private OracleBooleanSpTestsFactory factory;

    @Before
    public void prepare() {
        output = new SpGeneratorOutput();
        factory = new OracleBooleanSpTestsFactory(output);
    }

    private void addSpParameter(List<OracleSpParameter> params,
            String name, int direction) {
        params.add(factory.makeSpParameter(name, direction));
    }

    private String loadWrapperSample(String spName) {
        return "crap";
    }

    @Test
    public void procedureWithBooleanInputParamTest() {
        String spName = "proc_1";
        List<OracleSpParameter> args = new ArrayList<OracleSpParameter>();
        addSpParameter(args, "p_arg1", DbParameterAccessor.INPUT);
        OracleBooleanSpCommand command = factory.makeSpCommand(spName, args);

        String expectedResult = loadWrapperSample(spName);
        command.generate();
        String actual = command.toString();

        assertEquals(expectedResult, actual);
    }
}

