package dbfit.environment;

import static dbfit.environment.SqlServerTypeNameNormaliser.normaliseTypeName;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

public class SqlServerTypeNameNormaliserTest {

    @Test
    public void canParseCleanParameter() {
        assertThat(normaliseTypeName("INT"), is("INT"));
    }

    @Test
    public void convertsToUpperCase() {
        assertThat(normaliseTypeName("int"), is("INT"));
    }

    @Test
    public void trimsWhitespace() {
        assertThat(normaliseTypeName("  int "), is("INT"));
    }

    @Test
    public void spacesAreDelimiters() {
        assertThat(normaliseTypeName("INT Whatever"), is("INT"));
    }

    @Test
    public void parenthesesAreDelimiters() {
        assertThat(normaliseTypeName("INT(WHATEVER"), is("INT"));
    }

    @Test
    public void firstDelimiterWins() {
        assertThat(normaliseTypeName(" INT (WHATEVER"), is("INT"));
        assertThat(normaliseTypeName(" INT WHAT(EVER"), is("INT"));
        assertThat(normaliseTypeName(" INT WHAT ( EVER"), is("INT"));
    }

    @Test
    public void returnsBlankWhenBeginsWithDelimiters() {
        assertThat(normaliseTypeName(" ( INT"), is(""));
        assertThat(normaliseTypeName("( INT"), is(""));
    }

    @Test(expected = Exception.class)
    public void failsOnNull() {
        assertThat(normaliseTypeName(null), is("SHOULDN'T GET TO HERE"));
    }
}
