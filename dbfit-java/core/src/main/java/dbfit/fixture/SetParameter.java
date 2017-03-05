package dbfit.fixture;

import fit.Parse;

import static dbfit.util.SymbolUtil.isSymbolGetter;

/**
 * simple wrapper for fixture symbol value setting,
 * which takes care of NULL keyword and symbol loading
 *
 */
public class SetParameter extends fit.Fixture {
    private static Object eval(String value) {
        if (value == null || "null".equals(value.toLowerCase())) {
            return null;
        } else if (isSymbolGetter(value)) {
            return dbfit.util.SymbolUtil.getSymbol(value);
        } else {
            return value;
        }
    }

    public static void setParameter(String name, String value) {
        dbfit.util.SymbolUtil.setSymbol(name, eval(value));
    }

    @Override
    public void doTable(Parse table) {
        if (args.length != 2) throw new UnsupportedOperationException("Set parameter requires two arguments");
        setParameter(args[0], args[1]);
    }
}
