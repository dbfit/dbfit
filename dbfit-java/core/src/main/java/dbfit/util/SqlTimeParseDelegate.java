package dbfit.util;

public class SqlTimeParseDelegate {
    public static Object parse(String s) throws Exception {
        return java.sql.Time.valueOf(s);
    }
}
