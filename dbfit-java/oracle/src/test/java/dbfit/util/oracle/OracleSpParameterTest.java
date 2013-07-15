package dbfit.util.oracle;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import dbfit.util.Direction;
import static dbfit.util.Direction.*;
import static dbfit.util.oracle.OracleBooleanSpTestsFactory.*;
import static org.junit.Assert.assertEquals;

public class OracleSpParameterTest {
    private Map<Direction, String> expectedDirections;
    private SpGeneratorOutput output;
    private OracleBooleanSpTestsFactory factory;
    private OracleSpParameter pin;
    private Map<String, OracleSpParameter> spParams;

    public void initExpectedDirections() {
        expectedDirections = new HashMap<Direction, String>();
        expectedDirections.put(INPUT, "IN");
        expectedDirections.put(OUTPUT, "OUT");
        expectedDirections.put(INPUT_OUTPUT, "IN OUT");
        expectedDirections.put(RETURN_VALUE, "RETURN");
    }

    private void initOracleSpParameters() {
        spParams = factory.createSampleSpParameters();
    }

    @Before
    public void prepare() {
        output = new SpGeneratorOutput();
        factory = new OracleBooleanSpTestsFactory(output);

        initOracleSpParameters();
        initExpectedDirections();
    }

    @Test
    public void inputParameterDirectionNameTest() {
        HashMap<Integer, String> expectedNames = new HashMap<Integer, String>();

        int i = 1;
        for (Map.Entry<Direction, String> entry: expectedDirections.entrySet()) {
            OracleSpParameter p = factory.makeSpParameter("p_" + i, entry.getKey());
            assertEquals(entry.getValue(), p.getDirectionName());
            ++i;
        }
    }

    private void checkShortDirectionName(Direction direction, String expected) {
        OracleSpParameter p = factory.makeSpParameter("p1", direction);
        assertEquals(p.getShortDirectionName(), expected);
    }

    @Test
    public void inputParameterShortDirectionNameTest() {
        checkShortDirectionName(INPUT, "in");
        checkShortDirectionName(OUTPUT, "out");
        checkShortDirectionName(INPUT_OUTPUT, "inout");
        checkShortDirectionName(RETURN_VALUE, "ret");
    }

    private void checkParameterDeclaration(String paramName, String expected) {
        OracleSpParameter arg = spParams.get(paramName);
        arg.declareArgument();

        assertEquals(expected, arg.toString());
    }

    private void checkVariableDeclaration(String paramName, String expected) {
        OracleSpParameter arg = spParams.get(paramName);
        arg.declareVariable();

        assertEquals(expected, arg.toString().trim());
    }

    @Test
    public void inputParameterDeclareArgumentChrTest() {
        checkParameterDeclaration(SP_ARG_CHR_IN, "z_" + SP_ARG_CHR_IN + " IN VARCHAR2");
    }

    @Test
    public void inputParameterDeclareArgumentBooleanTest() {
        checkParameterDeclaration(SP_ARG_BOOL_IN, "z_" + SP_ARG_BOOL_IN + " IN BOOLEAN");
    }

    @Test
    public void outputParameterDeclareArgumentBooleanTest() {
        checkParameterDeclaration(SP_ARG_BOOL_OUT, "z_" + SP_ARG_BOOL_OUT + " OUT VARCHAR2");
    }

    @Test
    public void inoutParameterDeclareArgumentBooleanTest() {
        checkParameterDeclaration(SP_ARG_BOOL_INOUT, "z_" + SP_ARG_BOOL_INOUT + " IN OUT VARCHAR2");
    }

    @Test
    public void inputParameterDeclareArgumentNumberTest() {
        checkParameterDeclaration(SP_ARG_NUM_IN, "z_" + SP_ARG_NUM_IN + " IN NUMBER");
    }

    @Test
    public void variableDeclarChrInTest() {
        checkVariableDeclaration(SP_ARG_CHR_IN, "");
    }

    @Test
    public void variableDeclareBooleanInTest() {
        checkVariableDeclaration(SP_ARG_BOOL_IN, "");
    }

    @Test
    public void variableDeclareBooleanOutTest() {
        checkVariableDeclaration(SP_ARG_BOOL_OUT,
            "z_v_" + SP_ARG_BOOL_OUT + "_out BOOLEAN;");
    }

    @Test
    public void variableDeclareBooleanInoutTest() {
        checkVariableDeclaration(SP_ARG_BOOL_INOUT,
            "z_v_" + SP_ARG_BOOL_INOUT + "_inout BOOLEAN := z_chr2bool( z_" + SP_ARG_BOOL_INOUT + " );");
    }

    @Test
    public void variableDeclareNumberInTest() {
        checkVariableDeclaration(SP_ARG_NUM_IN, "");
    }

    @Test
    public void boolArgInCallShouldBeWrapped() {
        OracleSpParameter arg = spParams.get(SP_ARG_BOOL_IN);

        assertEquals("z_chr2bool( ? )", arg.getCallArgument());
    }

    @Test
    public void boolArgOutCallShouldNotBeWrapped() {
        OracleSpParameter arg = spParams.get(SP_ARG_BOOL_OUT);

        assertEquals("?", arg.getCallArgument());
    }

   @Test
    public void testAssignVariable() {
        OracleSpParameter p = factory.makeSpParameter("p", OUTPUT, "BOOLEAN", "z");

        p.assignOutputVariable();
        assertEquals("z_p := z_bool2chr( z_v_p_out );", p.toString().trim());
    }
}

