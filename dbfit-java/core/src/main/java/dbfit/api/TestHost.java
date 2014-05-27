package dbfit.api;

public interface TestHost {
	public Object getSymbolValue(String symbolName);
	public Class<?> getSymbolType(String symbolName);
	public void setSymbolValue(String symbolName, Object value, Class clazz);
	public void clearSymbols();

}