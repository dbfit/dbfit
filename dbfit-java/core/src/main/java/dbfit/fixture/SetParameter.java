package dbfit.fixture;

import fit.Parse;

import dbfit.util.ValueParser;

/**
 * simple wrapper for fixture symbol value setting,
 * which takes care of NULL keyword and symbol loading
 *
 */
public class SetParameter extends fit.Fixture {
    private static final ValueParser parser = new ValueParser();

    public static void setParameter(String name, String value) {
        dbfit.util.SymbolUtil.setSymbol(name, parser.parse(value));
    }

    public static void setParameter(String name, String value, String parseDelegate) {
        dbfit.util.SymbolUtil.setSymbol(name, parser.parse(value, parseDelegate));
    }

    @Override
    public void doTable(Parse table) {
        if (args.length == 2) {
            setParameter(args[0], args[1]);
        } else if (args.length == 3) {
            setParameter(args[0], args[1], args[2]);
        } else {
            throw new UnsupportedOperationException("Set parameter requires two or three arguments");
        }
    }
}
