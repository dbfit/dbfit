package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OracleSpParameterTest {
    private Map<Integer, String> expectedDirections;
    private SpGeneratorOutput output;
    private OracleBooleanSpTestsFactory factory;
    private OracleSpParameter pin;

    public void initExpectedDirections() {
        expectedDirections = new HashMap<Integer, String>();
        expectedDirections.put(DbParameterAccessor.INPUT, "IN");
        expectedDirections.put(DbParameterAccessor.OUTPUT, "OUT");
        expectedDirections.put(DbParameterAccessor.INPUT_OUTPUT, "IN OUT");
        expectedDirections.put(DbParameterAccessor.RETURN_VALUE, "RETURN");
    }

    @Before
    public void prepare() {
        initExpectedDirections();

        output = new SpGeneratorOutput();
        factory = new OracleBooleanSpTestsFactory(output);

        pin = factory.makeSpParameter("p_1_in", DbParameterAccessor.INPUT);
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

    @Test
    public void inputParameterDeclareArgumentTest() {
        pin.declareArgument();
        String result = pin.toString();
        assertEquals("p_1_in IN VARCHAR2", result);
    }
}

