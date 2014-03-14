package dbfit.environment;

import dbfit.util.Direction;
import static dbfit.util.Direction.*;
import static dbfit.test.matchers.IsParameter.*;

import org.junit.Test;
import static org.junit.Assert.assertThat;

import java.util.List;

public class MySqlProcedureParametersParserTest {
    private MySqlProcedureParametersParser parser =
        new MySqlProcedureParametersParser();
    private List<ParamDescriptor> params;

    @Test
    public void canParseParametersWithoutOptions() {
        parse("p1 decimal, p2 number");

        assertThat(params, containsParameters(
                    p("p1", INPUT, "decimal"),
                    p("p2", INPUT, "number")));
    }

    @Test
    public void canParseParametersWithLength() {
        parse("p1 varchar2(20), in p2 decimal ( 6 )");

        assertThat(params, containsParameters(
                    p("p1", INPUT, "varchar2"),
                    p("p2", INPUT, "decimal")));
    }

    @Test
    public void canParseParametersWithDirectionSpecifiers() {
        parse("p1 varchar2(20), IN p2 decimal(6), inout p3 int, out p4 decimal");

        assertThat(params, containsParameters(
                    p("p1", INPUT, "varchar2"),
                    p("p2", INPUT, "decimal"),
                    p("p3", INPUT_OUTPUT, "int"),
                    p("p4", OUTPUT, "decimal")));
    }

    @Test
    public void canParseParametersWithPrecisionAndScale() {
        parse("p1 decimal(6, 2), out p2 decimal(6,3)");

        assertThat(params, containsParameters(
                    p("p1", INPUT, "decimal"),
                    p("p2", OUTPUT, "decimal")));
    }

    @Test
    public void canParseSingleParameter() {
        assertThat(parse("p1 int"), containsParameters(p("p1", INPUT, "int")));
    }

    @Test
    public void canParseReturnValue() {
        assertThat(parseReturn("decimal (6, 3)"), hasReturnType("decimal"));
    }

    // Helpers

    private List<ParamDescriptor> parse(String parametersList) {
        params = parser.parseParameters(parametersList);
        return params;
    }

    private ParamDescriptor parseReturn(String returnTypeExpression) {
        return parser.parseReturnType(returnTypeExpression);
    }

    private ParamDescriptor p(String name, Direction direction, String type) {
        return new ParamDescriptor(name, direction, type);
    }
}
