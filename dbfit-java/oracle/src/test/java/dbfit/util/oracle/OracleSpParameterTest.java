package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;
import static dbfit.util.DbParameterAccessor.INPUT;
import static dbfit.util.DbParameterAccessor.OUTPUT;
import static dbfit.util.DbParameterAccessor.INPUT_OUTPUT;
import static dbfit.util.DbParameterAccessor.RETURN_VALUE;

import static dbfit.util.oracle.OracleBooleanSpTestsFactory.*;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OracleSpParameterTest {
    public static final String SP_ARG_CHR_IN = "p_1_in";
    public static final String SP_ARG_BOOL_IN = "p_bool_in";
    public static final String SP_ARG_NUM_IN = "p_num_in";

    private Map<Integer, String> expectedDirections;
    private SpGeneratorOutput output;
    private OracleBooleanSpTestsFactory factory;
    private OracleSpParameter pin;
    private Map<String, OracleSpParameter> spParams;

    public void initExpectedDirections() {
        expectedDirections = new HashMap<Integer, String>();
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
        for (Map.Entry<Integer, String> entry: expectedDirections.entrySet()) {
            OracleSpParameter p = factory.makeSpParameter("p_" + i, entry.getKey());
            assertEquals(entry.getValue(), p.getDirectionName());
            ++i;
        }
    }

    private void checkShortDirectionName(int direction, String expected) {
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

    @Test
    public void inputParameterDeclareArgumentChrTest() {
        checkParameterDeclaration(SP_ARG_CHR_IN, "z_" + SP_ARG_CHR_IN + " IN VARCHAR2");
    }

    @Test
    public void inputParameterDeclareArgumentBooleanTest() {
        checkParameterDeclaration(SP_ARG_BOOL_IN, "z_" + SP_ARG_BOOL_IN + " IN VARCHAR2");
    }

    @Test
    public void inputParameterDeclareArgumentNumberTest() {
        checkParameterDeclaration(SP_ARG_NUM_IN, "z_" + SP_ARG_NUM_IN + " IN NUMBER");
    }

    @Test
    public void boolArgInCallShouldBeWrapped() {
        OracleSpParameter arg = spParams.get(SP_ARG_BOOL_IN);
        arg.genWrapperCallArgument();

        assertEquals("z_chr2bool( ? )", arg.toString());
    }

    @Test
    public void boolArgOutCallShouldNotBeWrapped() {
        OracleSpParameter arg = spParams.get(SP_ARG_BOOL_OUT);
        arg.genWrapperCallArgument();

        assertEquals("?", arg.toString());
    }

   @Test
    public void testAssignVariable() {
        OracleSpParameter p = factory.makeSpParameter("p", OUTPUT, "BOOLEAN", "z");

        p.assignOutputVariable();
        assertEquals("z_p := z_bool2chr( z_v_p_out );", p.toString().trim());
    }
}

