package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.apache.commons.io.IOUtils;

public class OracleBooleanSpCommandTest {

    private SpGeneratorOutput output;
    private OracleBooleanSpTestsFactory factory;

    @Before
    public void prepare() {
        output = new SpGeneratorOutput();
        factory = new OracleBooleanSpTestsFactory(output);
    }

    private void addSpParameter(List<OracleSpParameter> params,
            String name, int direction, String dataType, String prefix) {
        params.add(factory.makeSpParameter(name, direction, dataType, prefix));
    }

    @Test
    public void procedureWithBooleanInputParamTest() throws IOException {
        String spName = "proc_1";
        List<OracleSpParameter> args = new ArrayList<OracleSpParameter>();
        addSpParameter(args, "p_arg1", DbParameterAccessor.INPUT, "BOOLEAN", "t");
        OracleBooleanSpCommand command = factory.makeSpCommand(spName, args);

        String expectedResult = loadWrapperSample("proc_1_1_bool_in.pls");
        command.generate();
        String actual = command.toString();

        assertEquals(expectedResult, actual);
    }

    private String loadWrapperSample(String sampleFile) throws IOException {
        return loadResource(sampleFile);
    }

    private String loadResource(String resource) throws IOException {
        InputStream in = getClass().getResourceAsStream(resource);
        try {
            return IOUtils.toString(in, "UTF-8");
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
}

