package dbfit.util;

import java.math.BigDecimal;


import dbfit.api.TestHost;
import dbfit.util.BigDecimalParseDelegate;
import dbfit.util.SqlDateParseDelegate;
import dbfit.util.SqlTimestampParseDelegate;
import fit.TypeAdapter;

public class FitNesseTestHost implements TestHost{

	static{
		TypeAdapter.registerParseDelegate(BigDecimal.class, BigDecimalParseDelegate.class);
		TypeAdapter.registerParseDelegate(java.sql.Date.class, SqlDateParseDelegate.class);
		TypeAdapter.registerParseDelegate(java.sql.Timestamp.class, SqlTimestampParseDelegate.class);
	}
	@Override
	public Object getSymbolValue(String symbolName) {
		return SymbolUtil.getSymbol(symbolName);
	}
	@Override
	public void setSymbolValue(String symbolName, Object value) {
		SymbolUtil.setSymbol(symbolName, value);
	}
	@Override
	public void clearSymbols() {
		SymbolUtil.clearSymbols();
	}
	private FitNesseTestHost(){
		
	}
	private final static FitNesseTestHost instance=new FitNesseTestHost();
	public static TestHost getInstance(){
		return instance;
	}
}
