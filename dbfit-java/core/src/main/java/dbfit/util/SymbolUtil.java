package dbfit.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/** ugly workaround for fit change in release 200807, which internally converts NULL into a string value "null";
 * for db access, we need to make a difference between NULL and "null" so this class provides a centralised
 * place for the change; for dbfit fixtures use this class to access symbols rather than directly fit.fixture
 */
public class SymbolUtil {
    private static final Object dbNull = new Object();
    private static HashMap<String, Class<?>> SymbolType = new HashMap<String, Class<?>>();

    //public static void setSymbol(String name, Object value, Class<?> clazz) {
    public static void setSymbol(String name, Object value) {
        System.out.println("SymbolUtil: setSymbol: entering");
        System.out.println("SymbolUtil: setSymbol: name: " + name + ", value");
        System.out.println("SymbolUtil: setSymbol: value == null: " + (value == null));
        fit.Fixture.setSymbol(getSymbolName(name), value == null ? dbNull : value);
        //SymbolType.put(name, clazz);      
    }

    public static Object getSymbol(String name) {
        System.out.println("SymbolUtil: getSymbol: entering");
        System.out.println("SymbolUtil: getSymbol: name: " + name);
        Object value = fit.Fixture.getSymbol(getSymbolName(name));
        System.out.println("SymbolUtil: getSymbol: value == null: " + (value == null));
        //System.out.println("SymbolUtil: getSymbol: value == dbnull: " + (value.equals(dbNull)));
        System.out.println("SymbolUtil: getSymbol: value: " + value);
        return (value == dbNull) ? null : value;
    }

    public static Object getSymbolType(String name) {
        System.out.println("SymbolUtil: getSymbolType: entering");
        System.out.println("SymbolUtil: getSymbolType: name: " + name);
        return SymbolType.get(getSymbolName(name));
    }
    
    public static void clearSymbols() {
        fit.Fixture.ClearSymbols();
        SymbolType.clear();
    }

    public static DataTable getDataTable(String symbolName) {
        Object o = getSymbol(symbolName);

        if (o == null) {
            throw new UnsupportedOperationException(
                    "Cannot load a stored query from " + symbolName);
        }

        if (o instanceof DataTable) {
            return (DataTable) o;
        }

        try {
            if (o instanceof ResultSet) {
                return new DataTable((ResultSet) o);
            }
        } catch (SQLException e) {
            throw new UnsupportedOperationException(
                    "Cannot load stored query from " + symbolName, e);
        }

        throw new UnsupportedOperationException(
                "Cannot load stored query from " + symbolName +
                " - object type is " + o.getClass().getName());
    }

    public static String getSymbolName(String symbolFullName) {
        System.out.println("SymbolUtil: getSymbolName: entering, with symbolFullName: " + symbolFullName);
        return ((isSymbolGetter(symbolFullName) || isSymbolSetter(symbolFullName))
                ? symbolFullName.substring(2)
                : symbolFullName).trim();
    }

    public static boolean isSymbolGetter(String text) {
        return text != null && text.startsWith("<<");
    }

    public static boolean isSymbolSetter(String text) {
        return text != null && text.startsWith(">>");
    }
}
