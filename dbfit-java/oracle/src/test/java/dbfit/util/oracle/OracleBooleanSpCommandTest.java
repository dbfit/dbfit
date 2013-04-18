package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;
import static dbfit.util.DbParameterAccessor.*;
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
    private static final String SP_PROC_BOOL_OUT = "proc_3_bool_out";
    private static final String SP_PROC_BOOL_OUT_BOOL_IN = "proc_4_bool_out_bool_in";

    private SpGeneratorOutput output;
    private OracleBooleanSpTestsFactory factory;
    private OracleBooleanSpCommand spProc1;
    private OracleBooleanSpCommand spProc2;
    private OracleBooleanSpCommand funcBoolInRetNum;
    private OracleBooleanSpCommand spProc3BoolOut;
    private OracleBooleanSpCommand spProc4BoolOutBoolIn;
    private Map<String, OracleSpParameter> spParams;

    @Before
    public void prepare() {
        output = new SpGeneratorOutput();
        factory = new OracleBooleanSpTestsFactory(output);
        spParams = factory.createSampleSpParameters();
        spProc1 = factory.getSpCommandBuilder(SP_PROC_1)
            .withBooleanArgument("p_arg1", INPUT)
            .build();
           
        spProc2 = factory.getSpCommandBuilder(SP_PROC_2)
            .withBooleanArgument("p_arg1", INPUT)
            .withArgument("p_arg2", INPUT, "NUMBER")
            .build();

        funcBoolInRetNum = factory.getSpCommandBuilder(SP_F_BOOL_IN_RET_NUM)
            .withBooleanArgument("p_arg1", INPUT)
            .withReturnValue("NUMBER")
            .build();

        spProc3BoolOut = createProcWithBooleanOutParam();

        spProc4BoolOutBoolIn = factory.getSpCommandBuilder(SP_PROC_BOOL_OUT_BOOL_IN)
            .withBooleanArgument("p_arg1", OUTPUT)
            .withBooleanArgument("p_arg2", INPUT)
            .build();
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
    public void procedureWithBooleanOutTest() throws IOException {
        OracleBooleanSpCommand command = spProc3BoolOut;
        String expectedResult = loadWrapperSample("proc_3_1_bool_out.pls");

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
    public void wrapperCallFunctionWithBooleanInAndNumReturnTest() throws IOException {
        OracleBooleanSpCommand command = funcBoolInRetNum;

        command.setPrefix("t");
        String actual = command.getWrapperCall();
        String expectedResult = "? := " + SP_F_BOOL_IN_RET_NUM + "( t_chr2bool( ? ) )";

        assertEquals(expectedResult, actual);
    }

    @Test
    public void wrapperHeaderWithBooleanOutputTest() {
        OracleBooleanSpCommand command = spProc3BoolOut;

        command.setPrefix("t");
        String actual = command.getWrapperHeader();
        String expectedResult = "procedure t_wrapper( t_p1 OUT VARCHAR2 )";

        assertEquals(expectedResult, actual.trim());
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

