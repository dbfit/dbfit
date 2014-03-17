package dbfit.util;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class SqlTimeParseDelegate {
    static final SimpleDateFormat FMT_S = new SimpleDateFormat("HH:mm:ss");
    static final SimpleDateFormat FMT_MS = new SimpleDateFormat("HH:mm:ss.S");

    public static Object parse(String s) throws Exception {
        return (s == null) ? null : parseTime(s);
    }

    private static Time parseTime(final String s) throws ParseException {
        SimpleDateFormat df = s.contains(".") ? FMT_MS : FMT_S;
        return new Time(df.parse(s).getTime());
    }
}
