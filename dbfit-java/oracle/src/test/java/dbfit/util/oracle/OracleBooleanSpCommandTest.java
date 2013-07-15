package dbfit.util.oracle;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static dbfit.util.Direction.*;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;

public class OracleBooleanSpCommandTest {

    private static final String SP_PROC_1 = "proc_1";
    private static final String SP_PROC_2 = "proc_2";
    private static final String SP_F_BOOL_IN_RET_NUM = "f_bool_in_ret_num";
    private static final String SP_F_BOOL_IN_NUM_INOUT_RET_NUM = "f_bool_in_num_inout_ret_num";
    private static final String SP_PROC_BOOL_OUT = "proc_3_bool_out";
    private static final String SP_PROC_BOOL_OUT_BOOL_IN = "proc_4_bool_out_bool_in";
    private static final String SP_PROC_BOOL_INOUT = "proc_5_bool_inout";
    private static final String SP_F_RET_BOOL = "f_ret_true";
    private static final String SP_F_BOOL_IN_RET_BOOL = "f_bool_in_ret_bool";
    private static final String SP_F_BOOL_OUT_RET_BOOL = "f_bool_out_ret_bool";
    private static final String SP_F_BOOL_INOUT_RET_BOOL = "f_bool_inout_ret_bool";
    private static final String SP_F_BOOL_OUT_RET_NUM = "f_bool_out_ret_num";
    private static final String SP_F_BOOL_ALL_CHR_IN_NUM_INOUT_RET_BOOL = "f_bool_all_mix_ret_bool";

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

    private void verifyWrapperHeaderVsExpectedResult(
                OracleBooleanSpTestsFactory.OracleSpCommandBuilder builder,
                String expectedResult) {
        OracleBooleanSpCommand command = builder.withPrefix("t").build();
        command.getWrapper().genWrapperHeader();
        String actual = command.toString();

        assertEquals(expectedResult, actual.trim());
    }

    private void verifyGeneratedWrapperCallVsExpectedResult(
                OracleBooleanSpTestsFactory.OracleSpCommandBuilder builder,
                String expectedResult) {
        OracleBooleanSpCommand command = builder.withPrefix("t").build();
        command.genCall();
        String actual = command.toString();

        assertEquals(expectedResult, actual);
    }

    private void verifyGeneratedWrapperWithExpectedResult(
                OracleBooleanSpTestsFactory.OracleSpCommandBuilder builder,
                String expectedResult) {
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
    public void functionRetBooleanTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_F_RET_BOOL)
                .withReturnValue("BOOLEAN"),
                "func_8_ret_true.pls");
    }

    @Test
    public void functionBoolInRetBoolTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_F_BOOL_IN_RET_BOOL)
                .withBooleanArgument(INPUT)
                .withReturnValue("BOOLEAN"),
                "func_9_bool_in_ret_bool.pls");
    }

    @Test
    public void functionBoolOutRetBoolTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_F_BOOL_OUT_RET_BOOL)
                .withBooleanArgument(OUTPUT)
                .withReturnValue("BOOLEAN"),
                "func_10_bool_out_ret_bool.pls");
    }

    @Test
    public void functionBoolOutRetNumTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_F_BOOL_OUT_RET_NUM)
                .withBooleanArgument(OUTPUT)
                .withReturnValue("NUMBER"),
                "func_11_bool_out_ret_num.pls");
    }

    @Test
    public void functionBoolInoutRetBoolTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_F_BOOL_INOUT_RET_BOOL)
                .withBooleanArgument(INPUT_OUTPUT)
                .withReturnValue("BOOLEAN"),
                "func_12_bool_inout_ret_bool.pls");
    }

    @Test
    public void functionBoolAllMixRetBoolTest() throws IOException {
        verifyGeneratedWrapperWithSavedResource(getCmdBuilder(SP_F_BOOL_ALL_CHR_IN_NUM_INOUT_RET_BOOL)
                .withBooleanArgument(INPUT)
                .withBooleanArgument(OUTPUT)
                .withBooleanArgument(INPUT_OUTPUT)
                .withArgument(INPUT, "VARCHAR2")
                .withArgument(INPUT_OUTPUT, "NUMBER")
                .withReturnValue("BOOLEAN"),
                "func_13_bool_all_mix_ret_bool.pls");
    }

    @Test
    public void wrapperCallProcedureWithBooleanAndNumInputsTest() {
        verifyGeneratedWrapperCallVsExpectedResult(getCmdBuilder(SP_PROC_2)
                .withBooleanArgument(INPUT)
                .withArgument(INPUT, "NUMBER"),
                SP_PROC_2 + "( t_chr2bool( ? ), ? )");
    }

    @Test
    public void wrapperCallFunctionWithBooleanInAndNumReturnTest() {
        verifyGeneratedWrapperCallVsExpectedResult(getCmdBuilder(SP_F_BOOL_IN_RET_NUM)
            .withBooleanArgument(INPUT)
            .withReturnValue("NUMBER"),
            "? := " + SP_F_BOOL_IN_RET_NUM + "( t_chr2bool( ? ) )");
    }

    @Test
    public void wrapperCallFunctionWithBooleanReturnTest() {
        verifyGeneratedWrapperCallVsExpectedResult(getCmdBuilder(SP_F_RET_BOOL)
            .withReturnValue("BOOLEAN"),
            "? := t_bool2chr( " + SP_F_RET_BOOL + "() )");
    }

    @Test
    public void wrapperCallFunctionWithBooleanInBooleanReturnTest() {
        verifyGeneratedWrapperCallVsExpectedResult(getCmdBuilder(SP_F_BOOL_IN_RET_BOOL)
            .withBooleanArgument(INPUT)
            .withReturnValue("BOOLEAN"),
            "? := t_bool2chr( " + SP_F_BOOL_IN_RET_BOOL + "( t_chr2bool( ? ) ) )");
    }

    @Test
    public void wrapperCallFunctionWithBooleanOutBooleanReturnTest() {
        verifyGeneratedWrapperCallVsExpectedResult(getCmdBuilder(SP_F_BOOL_OUT_RET_BOOL)
            .withBooleanArgument(OUTPUT)
            .withReturnValue("BOOLEAN"),
            "? := t_bool2chr( t_wrapper( ? ) )");
    }

    @Test
    public void wrapperCallFunctionWithBooleanInoutBooleanReturnTest() {
        verifyGeneratedWrapperCallVsExpectedResult(getCmdBuilder(SP_F_BOOL_INOUT_RET_BOOL)
            .withBooleanArgument(OUTPUT)
            .withReturnValue("BOOLEAN"),
            "? := t_bool2chr( t_wrapper( ? ) )");
    }

    @Test
    public void wrapperHeaderProcBooleanOutputTest() {
        verifyWrapperHeaderVsExpectedResult(getCmdBuilder(SP_PROC_BOOL_OUT)
            .withBooleanArgument(OUTPUT),
            "procedure t_wrapper( t_p1 OUT VARCHAR2 )");
    }

    @Test
    public void wrapperHeaderFuncBooleanOutReturnBooleanTest() {
        verifyWrapperHeaderVsExpectedResult(getCmdBuilder(SP_F_BOOL_OUT_RET_BOOL)
            .withBooleanArgument(OUTPUT)
            .withReturnValue("BOOLEAN"),
            "function t_wrapper( t_p1 OUT VARCHAR2 ) RETURN BOOLEAN");
    }

    @Test
    public void wrapperHeaderFuncBooleanInoutReturnBooleanTest() {
        verifyWrapperHeaderVsExpectedResult(getCmdBuilder(SP_F_BOOL_INOUT_RET_BOOL)
            .withBooleanArgument(INPUT_OUTPUT)
            .withReturnValue("BOOLEAN"),
            "function t_wrapper( t_p1 IN OUT VARCHAR2 ) RETURN BOOLEAN");
    }

    @Test
    public void wrapperHeaderFuncBooleanOutReturnNumberTest() {
        verifyWrapperHeaderVsExpectedResult(getCmdBuilder(SP_F_BOOL_OUT_RET_NUM)
            .withBooleanArgument(OUTPUT)
            .withReturnValue("NUMBER"),
            "function t_wrapper( t_p1 OUT VARCHAR2 ) RETURN NUMBER");
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

