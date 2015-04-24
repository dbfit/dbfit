package dbfit.environment;

import org.junit.Test;
import static org.junit.Assert.*;

public class TeradataTimestampPeriodTest {

    @Test
    public void equalsTest() {

        String fromStr = "2012-11-11 01:23:45";
        String toStr = "2014-06-02 12:13:14";

        java.sql.Timestamp F = null;
        java.sql.Timestamp T = null;

        try {
            F = (java.sql.Timestamp) SqlTimestampParseDelegate.parse(fromStr);
            T = (java.sql.Timestamp) SqlTimestampParseDelegate.parse(toStr);
        }
        catch (Exception e) {
            throw new Error("Parsing timestamp literals");
        }

        Object[] range = { F, T };
        TeradataTimestampPeriod tdp = new TeradataTimestampPeriod(range);
        TeradataTimestampPeriod tdp2 = new TeradataTimestampPeriod(range);
        assertTrue("Timestamp periods are not equal", tdp.equals(tdp2));
    }

    @Test
    public void notEqualsTest() {

        String fromStr = "2012-11-11 01:23:45";
        String toStr = "2014-06-02 12:13:14";

        java.sql.Timestamp F = null;
        java.sql.Timestamp T = null;
        
        try {
            F = (java.sql.Timestamp) SqlTimestampParseDelegate.parse(fromStr);
            T = (java.sql.Timestamp) SqlTimestampParseDelegate.parse(toStr);
        }
        catch (Exception e) {
            throw new Error("Parsing timestamp literals");
        }

        Object[] range = { F, T };
        TeradataTimestampPeriod tdp = new TeradataTimestampPeriod(range);
        Object[] range2 = { F, F };
        TeradataTimestampPeriod tdp2 = new TeradataTimestampPeriod(range2);
        assertFalse("Timestamp periods are equal", tdp.equals(tdp2));
    }

    @Test
    public void toStringTest() {

        String fromStr = "2012-11-11 01:23:45";
        String toStr = "2014-06-02 12:13:14";

        java.sql.Timestamp F = null;
        java.sql.Timestamp T = null;

        try {
            F = (java.sql.Timestamp) SqlTimestampParseDelegate.parse(fromStr);
            T = (java.sql.Timestamp) SqlTimestampParseDelegate.parse(toStr);
        }
        catch (Exception e) {
            throw new Error("Parsing timestamp literals");
        }

        Object[] range = { F, T };
        TeradataDatePeriod tdp = new TeradataDatePeriod(range);
        assertEquals("TimestampPeriod toString() return unexpected value", F.toString() + "," + T.toString(), tdp.toString());
    }
}
