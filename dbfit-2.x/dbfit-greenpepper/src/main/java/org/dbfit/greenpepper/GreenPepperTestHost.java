package org.dbfit.greenpepper;

import java.util.HashMap;
import java.util.Map;

import org.dbfit.core.TestHost;

public class GreenPepperTestHost implements TestHost{
	private Map<String, Object> symbols=new HashMap<String, Object>();
	@Override
	public void clearSymbols() {
		symbols.clear();
	}
	@Override
	public Object getSymbolValue(String symbolName) {
		return symbols.get(symbolName);
	}
	@Override
	public void setSymbolValue(String symbolName, Object value) {
		symbols.put(symbolName, value);	
	}
	private GreenPepperTestHost(){};
	private static GreenPepperTestHost instance=new GreenPepperTestHost();
	public static GreenPepperTestHost getInstance(){
		return instance;
	}
}
