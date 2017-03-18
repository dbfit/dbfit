package dbfit.util;

public class ValueParser {

    public Object parse(String text) {
        if (text == null || "null".equals(text.toLowerCase())) {
            return null;
        } else if (SymbolUtil.isSymbolGetter(text)) {
            return SymbolUtil.getSymbol(text);
        } else {
            return text;
        }
    }
}
