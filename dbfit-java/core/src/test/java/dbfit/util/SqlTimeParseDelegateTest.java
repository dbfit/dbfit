package dbfit.util;

import dbfit.util.SqlTimeParseDelegate;

import java.sql.Time;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

public class SqlTimeParseDelegateTest {
    @Test
    public void canParseSecondPrecisionTime() throws Exception {
        assertThat(parse("13:12:05"), is(Time.valueOf("13:12:05")));
    }

    @Test
    public void canParseMillisecondPrecisionTime() throws Exception {
        Time secTime = Time.valueOf("13:12:05");
        Time millisTime = new Time(secTime.getTime() + 1L); // add 1ms

        Time actual = parse("13:12:05.001");

        assertThat(actual, is(millisTime));
        assertThat(actual, is(not(secTime)));
    }

    @Test
    public void canParseNull() throws Exception {
        assertThat(parse(null), is(nullValue()));
    }

    @Test(expected = Exception.class)
    public void failsOnInvalidInput() throws Exception {
        parse("3.14");
    }

    @Test(expected = Exception.class)
    public void failsOnBlank() throws Exception {
        parse("");
    }

    private Time parse(String s) throws Exception {
        return (Time) SqlTimeParseDelegate.parse(s);
    }
}
