package dbfit.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import dbfit.api.TestHost;
import fit.TypeAdapter;

public class FitNesseTestHost implements TestHost {

    static {
        TypeAdapter.registerParseDelegate(BigDecimal.class, BigDecimalParseDelegate.class);
        TypeAdapter.registerParseDelegate(java.sql.Date.class, SqlDateParseDelegate.class);
        TypeAdapter.registerParseDelegate(java.sql.Timestamp.class, SqlTimestampParseDelegate.class);
        TypeAdapter.registerParseDelegate(java.sql.Time.class, SqlTimeParseDelegate.class);
        TypeNormaliserFactory.setNormaliser(BigDecimal.class, new BigDecimalNormaliser());
    }

    @Override
    public Object getSymbolValue(String symbolName) {
        System.out.println("FitNesseTestHost: getSymbolValue: entering");
        return SymbolUtil.getSymbol(symbolName);
    }

    //@Override
    public Class<?> getSymbolType(String symbolName) {
        System.out.println("FitNesseTestHost: getSymbolType: entering");
        System.out.println("FitNesseTestHost: getSymbolType: symbolName: " + symbolName);
        Class<?> clazz = SymbolUtil.getSymbolType(symbolName);
        System.out.println("FitNesseTestHost: getSymbolType: got class to return, class == null?: " + (clazz == null));
        return clazz;
    }
    
    @Override
    public void setSymbolValue(String symbolName, Object value, Class clazz) {
        System.out.println("FitNesseTestHost: setSymbolValue: entering");
        System.out.println("FitNesseTestHost: setSymbolValue: symbolName: " + symbolName);
        System.out.println("FitNesseTestHost: setSymbolValue: value is null: " + (value == null));
        SymbolUtil.setSymbol(symbolName, value, clazz);
    }
    
    @Override
    public void clearSymbols() {
        SymbolUtil.clearSymbols();
    }

    private FitNesseTestHost() {
    }

    private final static FitNesseTestHost instance = new FitNesseTestHost();

    public static TestHost getInstance() {
        return instance;
    }
}
