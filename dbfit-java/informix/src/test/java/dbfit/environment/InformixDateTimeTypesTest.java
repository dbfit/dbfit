package dbfit.environment;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;

public class InformixDateTimeTypesTest {

    @Test
    public void timeTypesTest() {
        assertEquals(getTimeTypes(), InformixDateTimeTypes.TIME_TYPES);
    }

    @Test
    public void timestampTypesTest() {
        assertEquals(getTimestampTypes(), InformixDateTimeTypes.TIMESTAMP_TYPES);
    }

    /*
     * Ordered list (largest first) of all time units from YEAR to FRACTION(5)
     */
    private List<String> getTimeUnits() {
        List<String> timeUnits = new ArrayList<>(Arrays.asList(
                "YEAR", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND"));

        // 5 levels of fractions
        for (int i = 1; i <= 5; ++i) {
            timeUnits.add("FRACTION(" + i + ")");
        }

        return timeUnits;
    }

    /*
     * Generate "DATETIME Ai TO Bj" combinations where Ai, Bj are time units
     * such as Ai >= Bi; Ai is between [Aastart, Aalast], Bj <= Bblast
     */
    private List<String> genDateTimePairs(String aStart, String aLast, String bLast) {
        List<String> timeUnits = getTimeUnits();
        int iaStart = timeUnits.indexOf(aStart);
        int iaLast = timeUnits.indexOf(aLast);
        int ibLast = timeUnits.indexOf(bLast);

        List<String> types = new ArrayList<String>();

        for (int ia = iaStart; ia <= iaLast; ++ia) {
            for (int ib = ia; ib <= ibLast; ++ib) {
                types.add(String.format("DATETIME %s TO %s", timeUnits.get(ia), timeUnits.get(ib)));
            }
        }

        return types;
    }

    private List<String> getTimeTypes() {
        return genDateTimePairs("HOUR", "SECOND", "SECOND");
    }

    private List<String> getTimestampTypes() {
        List<String> list = genDateTimePairs("YEAR", "SECOND", "FRACTION(5)");
        list.removeAll(getTimeTypes());
        return list;
    }
}
