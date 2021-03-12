package dbfit.environment;

import dbfit.util.Direction;
import dbfit.util.ParamDescriptor;
import org.junit.Test;

import java.util.List;

import static dbfit.test.matchers.IsParameter.*;
import static dbfit.util.Direction.*;
import static org.junit.Assert.assertThat;

public class SnowflakeProcedureParametersParserTest {
    private SnowflakeProcedureParametersParser parser =
        new SnowflakeProcedureParametersParser();
    private List<ParamDescriptor> params;

    @Test
    public void canParseMultipleParameters() {
        parse("(P1 VARCHAR, P2 VARCHAR, P3 decimal)");

        assertThat(params, containsParameters(
                    p("p1", INPUT, "varchar"),
                    p("p2", INPUT, "varchar"),
                    p("p3", INPUT, "decimal")));
    }

    @Test
    public void canParseSingleParameter() {
        assertThat(parse("p1 int"), containsParameters(p("p1", INPUT, "int")));
    }

    // Helpers

    private List<ParamDescriptor> parse(String parametersList) {
        params = parser.parseParameters(parametersList);
        return params;
    }

    private ParamDescriptor p(String name, Direction direction, String type) {
        return new ParamDescriptor(name, direction, type);
    }
}
