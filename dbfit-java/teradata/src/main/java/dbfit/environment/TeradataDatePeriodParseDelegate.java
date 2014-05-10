package dbfit.environment;

import java.text.DateFormat;

public class TeradataDatePeriodParseDelegate {
	
	private static DateFormat df = DateFormat.getDateInstance();	
	
	public static Object parse(String s) throws Exception {
		
		System.out.println("TeradataDatePeriodParseDelegate: parse: s: " + s);
		String[] periodParts = s.split(",");
		java.sql.Date F;
		java.sql.Date T;
		java.util.Date ParsedFrom;
		java.util.Date ParsedTo;
		
		try {
			//ParsedFrom = java.sql.Date.valueOf(periodParts[0]);
			F = java.sql.Date.valueOf(periodParts[0]);
		} catch (IllegalArgumentException iex) {
			java.util.Date ud = df.parse(periodParts[0]);
			//ParsedFrom = new java.sql.Date(ud.getTime());
			F = new java.sql.Date(ud.getTime());
		}
		
		try {
			//ParsedTo = java.sql.Date.valueOf(periodParts[1]);
			T = java.sql.Date.valueOf(periodParts[1]);
		} catch (IllegalArgumentException iex) {
			java.util.Date ud = df.parse(periodParts[1]);
			//ParsedTo = new java.sql.Date(ud.getTime());
			T = new java.sql.Date(ud.getTime());
		}
		
		Object[] data = { F, T }; 
		       
		//return new TeradataDatePeriod(ParsedFrom, ParsedTo);
		return new TeradataDatePeriod(data);
	}
}