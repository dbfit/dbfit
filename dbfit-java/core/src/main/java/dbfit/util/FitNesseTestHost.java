package dbfit.util;

import java.math.BigDecimal;

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
        return SymbolUtil.getSymbol(symbolName);
    }

    private FitNesseTestHost() {
    }

    private final static FitNesseTestHost instance = new FitNesseTestHost();

    public static TestHost getInstance() {
        return instance;
    }
}
