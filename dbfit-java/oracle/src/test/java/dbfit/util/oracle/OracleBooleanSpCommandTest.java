package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.hamcrest.core.IsNot.not;
import org.apache.commons.io.IOUtils;

public class OracleBooleanSpCommandTest {

    private static final String SP_PROC_1 = "proc_1";

    private SpGeneratorOutput output;
    private OracleBooleanSpTestsFactory factory;
    private OracleBooleanSpCommand spProc1;

    @Before
    public void prepare() {
        output = new SpGeneratorOutput();
        factory = new OracleBooleanSpTestsFactory(output);
        spProc1 = createProcWithBooleanInParam();
    }

    private void addSpParameter(List<OracleSpParameter> params,
            String name, int direction, String dataType, String prefix) {
        params.add(factory.makeSpParameter(name, direction, dataType, prefix));
    }

    private OracleBooleanSpCommand createProcWithBooleanInParam() {
        List<OracleSpParameter> args = new ArrayList<OracleSpParameter>();
        addSpParameter(args, "p_arg1", DbParameterAccessor.INPUT, "BOOLEAN", "t");

        return factory.makeSpCommand(SP_PROC_1, args);
    }

    @Test
    public void procedureWithBooleanInputParamTest() throws IOException {
        OracleBooleanSpCommand command = spProc1;
        String expectedResult = loadWrapperSample("proc_1_1_bool_in.pls");

        command.generate();
        String actual = command.toString();

        assertEquals(expectedResult, actual);
    }

    @Test
    public void prefixShouldNotConflictWithSpName() throws IOException {
        String prefix = spProc1.getPrefix().toLowerCase();

        assertFalse("prefix should not be empty", prefix.isEmpty());
        assertThat(SP_PROC_1.toLowerCase(), not(startsWith(prefix)));
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

