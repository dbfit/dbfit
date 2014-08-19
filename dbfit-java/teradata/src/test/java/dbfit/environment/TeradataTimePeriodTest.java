package dbfit.environment;

import java.sql.Time;

import org.junit.Test;
import static org.junit.Assert.*;

public class TeradataTimePeriodTest {

    @Test
    public void equalsTest() {

        String fromStr = "01:23:45";
        String toStr = "12:13:14";

        Time fromTime = null;
        Time toTime = null;

        fromTime = Time.valueOf(fromStr);
        toTime = Time.valueOf(toStr);

        Time[] range = {fromTime, toTime};
        TeradataTimePeriod tdp = new TeradataTimePeriod(range);
        TeradataTimePeriod tdp2 = new TeradataTimePeriod(range);
        assertTrue("Time periods are not equal", tdp.equals(tdp2));
    }

    @Test
    public void notEqualsTest() {

        String fromStr = "01:23:45";
        String toStr = "12:13:14";

        Time fromTime = null;
        Time toTime = null;

        fromTime = Time.valueOf(fromStr);
        toTime = Time.valueOf(toStr);

        Time[] range = {fromTime, toTime};
        TeradataTimePeriod tdp = new TeradataTimePeriod(range);
        Time[] range2 = {fromTime, fromTime};
        TeradataTimePeriod tdp2 = new TeradataTimePeriod(range2);
        assertFalse("Time periods are equal", tdp.equals(tdp2));
    }

    @Test
    public void toStringTest() {

        String fromStr = "01:23:45";
        String toStr = "12:13:14";

        Time fromTime = null;
        Time toTime = null;

        fromTime = Time.valueOf(fromStr);
        toTime = Time.valueOf(toStr);

        Time[] range = {fromTime, toTime};

        TeradataTimePeriod tdp = new TeradataTimePeriod(range);
        assertEquals("Time periods toString does return expected value", fromTime.toString() + "," + toTime.toString(), tdp.toString());
    }
}

