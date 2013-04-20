package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;
import static dbfit.util.DbParameterAccessor.*;
import static dbfit.util.oracle.OracleBooleanSpTestsFactory.*;

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
    private static final String SP_F_BOOL_IN_NUM_INOUT_RET_NUM = "f_bool_in_num_inout_ret_num";
    private static final String SP_PROC_BOOL_OUT = "proc_3_bool_out";
    private static final String SP_PROC_BOOL_OUT_BOOL_IN = "proc_4_bool_out_bool_in";
    private static final String SP_PROC_BOOL_INOUT = "proc_5_bool_inout";

    private SpGeneratorOutput output;
    private OracleBooleanSpTestsFactory factory;
    private OracleBooleanSpCommand spProc1;

    @Before
    public void prepare() {
        output = new SpGeneratorOutput();
        factory = new OracleBooleanSpTestsFactory(output);

        spProc1 = factory.getSpCommandBuilder(SP_PROC_1)
            .withBooleanArgument(INPUT)
            .build();
    }

    private OracleBooleanSpTestsFactory.OracleSpCommandBuilder getCmdBuilder(String procName) {
        return factory.getSpCommandBuilder(procName);
    }

    private void verifyGeneratedWrapperWithExpectedResult(
                OracleBooleanSpTestsFactory.OracleSpCommandBuilder builder,
                String expectedResult) throws IOException {
        OracleBooleanSpCommand command = builder.withPrefix("t").build();

        command.generate();
        String actual = command.toString();

        assertEquals(expectedResult, actual);
    }

    private void verifyGeneratedWrapperWithSavedResource(
                OracleBooleanSpTestsFactory.OracleSpCommandBuilder builder,
                String correctResultFilename) throws IOException {
        String expectedResult = loadWrapperSample(correctResultFilename);

        verifyGeneratedWrapperWithExpectedResult(builder, expectedResult);
    }

    @Test
    public void procedureWithBooleanInputParamTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_PROC_1)
                .withBooleanArgument(INPUT), 
                "proc_1_1_bool_in.pls");
    }

    @Test
    public void procedureWithBooleanAndNumInputsTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_PROC_2)
                .withBooleanArgument(INPUT)
                .withArgument(INPUT, "NUMBER"),
                "proc_2_1_bool_in_1_num_in.pls");
    }

    @Test
    public void procedureWithBooleanOutTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_PROC_BOOL_OUT)
                .withBooleanArgument(OUTPUT),
                "proc_3_1_bool_out.pls");
    }

    @Test
    public void procedureWithBooleanInBooleanOutTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_PROC_BOOL_OUT_BOOL_IN)
                .withBooleanArgument(OUTPUT)
                .withBooleanArgument(INPUT),
                "proc_4_1_bool_out_1_bool_in.pls");
    }

    @Test
    public void procedureWithBooleanInoutTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_PROC_BOOL_INOUT)
                .withBooleanArgument(INPUT_OUTPUT),
                "proc_5_1_bool_inout.pls");
    }

    @Test
    public void functionWithBooleanInRetNumTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_F_BOOL_IN_RET_NUM)
                .withBooleanArgument(INPUT)
                .withReturnValue("NUMBER"),
                "func_7_1_bool_in_ret_num.pls");
    }

    @Test
    public void functionWithBooleanInNumInoutRetNumTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_F_BOOL_IN_NUM_INOUT_RET_NUM)
                .withBooleanArgument(INPUT)
                .withArgument(INPUT_OUTPUT, "NUMBER")
                .withReturnValue("NUMBER"),
                "func_6_1_bool_in_1_num_inout_ret_num.pls");
    }

    @Test
    public void wrapperCallProcedureWithBooleanAndNumInputsTest() throws IOException {
        OracleBooleanSpCommand command = getCmdBuilder(SP_PROC_2)
                .withBooleanArgument(INPUT)
                .withArgument(INPUT, "NUMBER")
                .withPrefix("t")
                .build();

        String actual = command.getWrapperCall();
        String expectedResult = SP_PROC_2 + "( t_chr2bool( ? ), ? )";

        assertEquals(expectedResult, actual);
    }

    @Test
    public void wrapperCallFunctionWithBooleanInAndNumReturnTest() throws IOException {
        OracleBooleanSpCommand command = getCmdBuilder(SP_F_BOOL_IN_RET_NUM)
            .withBooleanArgument(INPUT)
            .withReturnValue("NUMBER")
            .withPrefix("t")
            .build();

        String actual = command.getWrapperCall();
        String expectedResult = "? := " + SP_F_BOOL_IN_RET_NUM + "( t_chr2bool( ? ) )";

        assertEquals(expectedResult, actual);
    }

    @Test
    public void wrapperHeaderWithBooleanOutputTest() {
        OracleBooleanSpCommand command = getCmdBuilder(SP_PROC_BOOL_OUT)
            .withBooleanArgument(OUTPUT)
            .withPrefix("t")
            .build();

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

