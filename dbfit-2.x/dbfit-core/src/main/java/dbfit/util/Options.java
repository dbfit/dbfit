package dbfit.util;

import java.util.HashMap;
import java.util.Map;

public class Options {
	public static void reset() {
		fixedLengthStringParsing = false;
		bindSymbols = true;
		debugLog = false;
	}

	private static boolean fixedLengthStringParsing = false;
	private static boolean bindSymbols = true;
	private static boolean debugLog = false;

	public static boolean isFixedLengthStringParsing() {
		return fixedLengthStringParsing;
	}

	public static boolean isBindSymbols() {
		return bindSymbols;
	}

	public static boolean isDebugLog() {
		return debugLog;
	}
	private static Map<String,String> freeOptions=new HashMap<String,String>();
	
	public static boolean is(String option){
		String normalname = NameNormaliser.normaliseName(option);
		if (!freeOptions.containsKey(normalname)) return false;
		return Boolean.parseBoolean(freeOptions.get(normalname));
	}
	public static void setOption(String name, String value) {
		String normalname = NameNormaliser.normaliseName(name);
		if ("fixedlengthstringparsing".equals(normalname)) {
			fixedLengthStringParsing = Boolean.parseBoolean(value);
		} else if ("bindsymbols".equals(normalname)) {
			bindSymbols = Boolean.parseBoolean(value);
		} else if ("debuglog".equals(normalname)) {
			debugLog = Boolean.parseBoolean(value);
		} else
			freeOptions.put(normalname,value);
	}
}