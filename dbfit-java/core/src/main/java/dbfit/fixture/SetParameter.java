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

    @Override
    public void doTable(Parse table) {
        if (args.length != 2) throw new UnsupportedOperationException("Set parameter requires two arguments");
        setParameter(args[0], args[1]);
    }
}
