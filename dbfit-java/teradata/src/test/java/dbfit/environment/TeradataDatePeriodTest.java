package dbfit.environment;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.*;

public class TeradataDatePeriodTest {

    @Test
    public void equalsTest() {

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
        TeradataDatePeriod tdp = new TeradataDatePeriod(range);
        TeradataDatePeriod tdp2 = new TeradataDatePeriod(range);
        assertTrue("Date periods are not equal", tdp.equals(tdp2));
    }

    @Test
    public void notEqualsTest() {

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
        TeradataDatePeriod tdp = new TeradataDatePeriod(range);
        Date[] range2 = {fromDate, fromDate};
        TeradataDatePeriod tdp2 = new TeradataDatePeriod(range2);
        assertFalse("Date periods are equal", tdp.equals(tdp2));
    }

    @Test
    public void toStringTest() {

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
        TeradataDatePeriod tdp = new TeradataDatePeriod(range);
        assertEquals("Date periods are not equal", fromDate.toString() + "," + toDate.toString(), tdp.toString());
    }
}
