package org.dbfit.core;

public interface TestHost {
	public Object getSymbolValue(String symbolName);
	public void setSymbolValue(String symbolName, Object value);
	public void clearSymbols();

}
