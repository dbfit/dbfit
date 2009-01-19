package org.dbfit.greenpepper.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dbfit.core.TestHost;

import com.greenpepper.ExecutionContext;

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
	public void copyFromExecutionContext(ExecutionContext ctx) {
		symbols.putAll(ctx.getAllVariables());	
	}
	public void copyToExecutionContext(ExecutionContext ctx){
		for(Entry<String,Object>value :symbols.entrySet()){
			ctx.setVariable(value.getKey(), value.getValue());
		}
	}
	private GreenPepperTestHost(){};
	private static GreenPepperTestHost instance=new GreenPepperTestHost();
	public static GreenPepperTestHost getInstance(){
		return instance;
	}
}
