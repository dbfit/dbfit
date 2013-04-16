package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;
import static dbfit.util.oracle.OracleBooleanSpTestsFactory.*;

import java.util.List;
import java.util.Map;
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
    private static final String SP_PROC_2 = "proc_2";
    private static final String SP_F_BOOL_IN_RET_NUM = "f_bool_in_ret_num";

    private SpGeneratorOutput output;
    private OracleBooleanSpTestsFactory factory;
    private OracleBooleanSpCommand spProc1;
    private OracleBooleanSpCommand spProc2;
    private OracleBooleanSpCommand funcBoolInRetNum;
    private Map<String, OracleSpParameter> spParams;

    @Before
    public void prepare() {
        output = new SpGeneratorOutput();
        factory = new OracleBooleanSpTestsFactory(output);
        spParams = factory.createSampleSpParameters();
        spProc1 = createProcWithBooleanInParam();
        spProc2 = createProcWithBooleanInAndNumInParam();
        funcBoolInRetNum = createFuncBoolInRetNum();
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
    private OracleBooleanSpCommand createProcWithBooleanInAndNumInParam() {
        List<OracleSpParameter> args = new ArrayList<OracleSpParameter>();
        addSpParameter(args, "p_arg1", DbParameterAccessor.INPUT, "BOOLEAN", "t");
        addSpParameter(args, "p_arg2", DbParameterAccessor.INPUT, "NUMBER", "t");

        return factory.makeSpCommand(SP_PROC_2, args);
    }

    private OracleBooleanSpCommand createFuncBoolInRetNum() {
        List<OracleSpParameter> args = new ArrayList<OracleSpParameter>();
        args.add(spParams.get(SP_ARG_BOOL_IN));

        return factory.makeSpCommand(SP_F_BOOL_IN_RET_NUM, args,
               spParams.get(SP_RETVAL_NUM));
    }

    @Test
    public void procedureWithBooleanInputParamTest() throws IOException {
        OracleBooleanSpCommand command = spProc1;
        String expectedResult = loadWrapperSample("proc_1_1_bool_in.pls");

        command.setPrefix("t");
        command.generate();
        String actual = command.toString();

        assertEquals(expectedResult, actual);
    }

    @Test
    public void procedureWithBooleanAndNumInputsTest() throws IOException {
        OracleBooleanSpCommand command = spProc2;
        String expectedResult = loadWrapperSample("proc_2_1_bool_in_1_num_in.pls");

        command.setPrefix("t");
        command.generate();
        String actual = command.toString();

        assertEquals(expectedResult, actual);
    }

    @Test
    public void wrapperCallProcedureWithBooleanAndNumInputsTest() throws IOException {
        OracleBooleanSpCommand command = spProc2;

        command.setPrefix("t");
        String actual = command.getWrapperCall();
        String expectedResult = SP_PROC_2 + "( t_chr2bool( ? ), ? )";

        assertEquals(expectedResult, actual);
    }

    @Test
    public void wrapperCallFunctionWithBooleanInandNumReturnTest() throws IOException {
        OracleBooleanSpCommand command = funcBoolInRetNum;

        command.setPrefix("t");
        String actual = command.getWrapperCall();
        String expectedResult = "? := " + SP_F_BOOL_IN_RET_NUM + "( t_chr2bool( ? ) )";

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

