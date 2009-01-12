package dbfit.util;

import java.text.DateFormat;

public class SqlDateParseDelegate {
	private static DateFormat df = DateFormat.getDateInstance();

	public static Object parse(String s) throws Exception {
		try {
			return java.sql.Date.valueOf(s);
		} catch (IllegalArgumentException iex) {
			java.util.Date ud = df.parse(s);
			return new java.sql.Date(ud.getTime());
		}
	}
}
