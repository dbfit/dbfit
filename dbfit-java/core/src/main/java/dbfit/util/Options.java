package dbfit.util;

import java.util.HashMap;
import java.util.Map;

public class Options {
    public static String OPTION_FIXED_LENGTH_STRING_PARSING = "fixedlengthstringparsing";
    public static String OPTION_BIND_SYMBOLS = "bindsymbols";
    public static String OPTION_DEBUG_LOG = "debuglog";
    public static String OPTION_AUTO_COMMIT = "autocommit";

    private static Map<String, String> options = new HashMap<String, String>();

    static {
        reset();
    }

    public static void reset() {
        options.clear();
        setOption(OPTION_FIXED_LENGTH_STRING_PARSING, "false");
        setOption(OPTION_BIND_SYMBOLS, "true");
        setOption(OPTION_DEBUG_LOG, "false");
        setOption(OPTION_AUTO_COMMIT, "false");
    }

    public static boolean isFixedLengthStringParsing() {
        return is(OPTION_FIXED_LENGTH_STRING_PARSING);
    }

    public static boolean isBindSymbols() {
        return is(OPTION_BIND_SYMBOLS);
    }

    public static boolean isDebugLog() {
        return is(OPTION_DEBUG_LOG);
    }

    public static boolean is(String option) {
        String normalname = NameNormaliser.normaliseName(option);
        if (!options.containsKey(normalname)) {
            return false;
        }
        return Boolean.parseBoolean(options.get(normalname));
    }

    public static String get(String option) {
        return options.get(NameNormaliser.normaliseName(option));
    }

    public static void setOption(String name, String value) {
        options.put(NameNormaliser.normaliseName(name), value);
    }
}
