package dbfit.environment;

import org.junit.Test;
import static org.junit.Assert.*;

public class TeradataTimestampPeriodParseDelegateTest {

    @Test
    public void parseTest() {

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
        TeradataTimestampPeriod tp = new TeradataTimestampPeriod(range);

        TeradataTimestampPeriod tp2 = null;
        try {
            tp2 = (TeradataTimestampPeriod) TeradataTimestampPeriodParseDelegate.parse(fromStr + "," + toStr);
        }
        catch (Exception e) {
            throw new Error("During TeradataTimestampPeriodParseDelegate.parse(" + fromStr + "," + toStr + ")");
        }
        
        assertTrue("Timestamp period parse does not produce expected object", tp.equals(tp2));
    }
}
