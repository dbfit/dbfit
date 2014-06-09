package dbfit.environment;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.*;

public class TeradataDatePeriodParseDelegateTest {

    @Test
    public void parseTest() {

        String fromStr = "2012-11-11";
        String toStr = "2014-06-02";

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = null;
        Date toDate = null;

        try {
            fromDate = dateFormat.parse(fromStr);
            toDate = dateFormat.parse(toStr);
        }
        catch (ParseException e) {
            throw new Error("TeradataDatePeriodTest: toStringTest: error parsing test input");
        }

        Date[] range = {fromDate, toDate};
        TeradataDatePeriod tp = new TeradataDatePeriod(range);

        TeradataDatePeriod tp2 = null;
        try {
            tp2 = (TeradataDatePeriod) TeradataDatePeriodParseDelegate.parse(fromStr + "," + toStr);
        }
        catch (Exception e) {
            throw new Error("During TeradataDatePeriodParseDelegate.parse(" + fromStr + "," + toStr + ")");
        }
        
        assertTrue("Date period parse does not produce expected object", tp.equals(tp2));
    }
}
