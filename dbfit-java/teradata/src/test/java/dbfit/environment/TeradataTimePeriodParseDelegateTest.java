package dbfit.environment;

import java.sql.Time;

import org.junit.Test;
import static org.junit.Assert.*;

public class TeradataTimePeriodParseDelegateTest {

    @Test
    public void parseTest() {

        String fromStr = "01:23:45";
        String toStr = "12:13:14";

        Time fromTime = Time.valueOf(fromStr);
        Time toTime = Time.valueOf(toStr);

        Time[] range = {fromTime, toTime};
        TeradataTimePeriod tp = new TeradataTimePeriod(range);

        TeradataTimePeriod tp2 = null;
        try {
            tp2 = (TeradataTimePeriod) TeradataTimePeriodParseDelegate.parse(fromStr + "," + toStr);
        }
        catch (Exception e) {
            throw new Error("During TeradataTimePeriodParseDelegate.parse(" + fromStr + "," + toStr + ")");
        }

        assertTrue("Time period parse does not produce expected object", tp.equals(tp2));
    }
}
