package dbfit.environment;

import java.sql.Time;

public class TeradataTimePeriodParseDelegate {
	
	public static Object parse(String s) throws Exception {
		
		System.out.println("TeradataTimePeriodParseDelegate: parse: s: " + s);
		String[] periodParts = s.split(",");
		
		//java.sql.Timestamp F = (java.sql.Timestamp) SqlDateParseDelegate.parse(periodParts[0]);
		//java.sql.Timestamp T = (java.sql.Timestamp) SqlDateParseDelegate.parse(periodParts[1]);
		
		//Object[] data = { F, T };
		
		//DateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		//Date date = sdf.parse(s);
		//System.out.println("Time: " + sdf.format(date));
		
		Time timeFrom = Time.valueOf(periodParts[0]);
		Time timeTo = Time.valueOf(periodParts[1]);
		
		Object[] data = { timeFrom, timeTo };
		       
		return new TeradataTimePeriod(data);
	}
}