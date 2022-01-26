package dbfit.util;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

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

    public Object parse(String text, Class<?> parseDelegateClass) {
        try {
            return findParsingMethod(parseDelegateClass).invoke(null, text);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Object parse(String text, String parseDelegateClassName) {
        try {
            return parse(text, Class.forName(parseDelegateClassName));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Method findParsingMethod(Class<?> cls) throws NoSuchMethodException {
        try {
            return cls.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {
            return cls.getMethod("parse", String.class);
        }
    }
}
