package dbfit.util;

import static dbfit.util.Direction.*;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Map;

public class DbParameterAccessorsMapBuilderTest {
    private int sqlType = java.sql.Types.VARCHAR;
    private Class<?> javaType = String.class;
    private DbParameterAccessorsMapBuilder params =
        new DbParameterAccessorsMapBuilder();

    @Test
    public void normalParametersAreCreatedOnProperPositions() {
        createParameterAccessor("p1", INPUT);
        createParameterAccessor("p2", OUTPUT);

        assertThat(getPositionOf("p1"), is(0));
        assertThat(getPositionOf("p2"), is(1));
    }

    @Test
    public void returnValueIsCreatedOnSpecialPosition() {
        createMixOfParametersAndReturnValues();

        assertThat(getPositionOf("r1"), is(-1));
        assertThat(getPositionOf("r2"), is(-1));
    }

    @Test
    public void positionIsNotAdvancedOnReturnValue() {
        createMixOfParametersAndReturnValues();

        assertThat(getPositionOf("p2"), is(1));
    }

    @Test
    public void includesAllAddedParameters() {
        createMixOfParametersAndReturnValues();

        assertThat(params.toMap().keySet(),
                containsInAnyOrder("r1", "p1", "r2", "p2"));
    }

    @Test
    public void normalisesMapKey() {
        createParameterAccessor("param 1", INPUT);
        DbParameterAccessor paramAccessor = params.toMap().get("param1");

        assertThat(paramAccessor, is(not(nullValue())));
        assertThat(paramAccessor.getName(), is("param 1"));
    }

    /* Helpers */

    private void createMixOfParametersAndReturnValues() {
        createParameterAccessor("r1", RETURN_VALUE);
        createParameterAccessor("p1", INPUT);
        createParameterAccessor("r2", RETURN_VALUE);
        createParameterAccessor("p2", OUTPUT);
    }

    private void createParameterAccessor(String name, Direction direction) {
        params.add(name, direction, sqlType, javaType);
    }

    private int getPositionOf(String name) {
        return params.toMap().get(name).getPosition();
    }
}
