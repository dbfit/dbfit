package dbfit.environment;

import java.sql.Time;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class MillisecondTimeNormaliserTest {
    private MillisecondTimeNormaliser normaliser = new MillisecondTimeNormaliser();
    private Time secTime = Time.valueOf("13:12:05");
    private Time millisTime = new Time(secTime.getTime() + 1L); // add 1ms

    @Test
    public void toStringExposesTimeUpToMilliseconds() {
        Time normalisedTime = (Time) normaliser.normalise(millisTime);
        String[] timeParts = normalisedTime.toString().split("\\.");

        assertEquals("13:12:05", timeParts[0]);
        assertEquals(1, Integer.parseInt(timeParts[1]));
        assertEquals(2, timeParts.length);
    }
}
