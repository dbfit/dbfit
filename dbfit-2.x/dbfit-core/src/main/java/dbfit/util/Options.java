package dbfit.util;

public class Options {
	public static void reset() {
		fixedLengthStringParsing = false;
		bindSymbols = true;
		debugLog = true;
	}

	private static boolean fixedLengthStringParsing = false;
	private static boolean bindSymbols = true;
	private static boolean debugLog = true;

	public static boolean isFixedLengthStringParsing() {
		return fixedLengthStringParsing;
	}

	public static boolean isBindSymbols() {
		return bindSymbols;
	}

	public static boolean isDebugLog() {
		return debugLog;
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
			throw new IllegalArgumentException(String.format("Unsupported option '%s'", normalname));
	}
}