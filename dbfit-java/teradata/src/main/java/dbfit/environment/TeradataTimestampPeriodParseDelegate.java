package dbfit.environment;

import java.text.DateFormat;

public class TeradataTimestampPeriodParseDelegate {
	
	private static DateFormat df = DateFormat.getDateTimeInstance();	
	
	public static Object parse(String s) throws Exception {
		
		System.out.println("TeradataTimestampPeriodParseDelegate: parse: s: " + s);
		String[] periodParts = s.split(",");
		
		java.sql.Timestamp F = (java.sql.Timestamp) SqlTimestampParseDelegate.parse(periodParts[0]);
		java.sql.Timestamp T = (java.sql.Timestamp) SqlTimestampParseDelegate.parse(periodParts[1]);
		
		Object[] data = { F, T }; 
		       
		return new TeradataTimestampPeriod(data);
	}
}