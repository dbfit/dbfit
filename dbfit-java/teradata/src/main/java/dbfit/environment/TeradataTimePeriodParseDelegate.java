package dbfit.environment;

import java.sql.Time;

public class TeradataTimePeriodParseDelegate {

    public static Object parse(String s) throws Exception {

        String[] periodParts = s.split(",");

        Time timeFrom = Time.valueOf(periodParts[0]);
        Time timeTo = Time.valueOf(periodParts[1]);

        Object[] data = { timeFrom, timeTo };

        return new TeradataTimePeriod(data);
    }
}