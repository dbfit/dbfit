package dbfit.util;

import fit.Fixture;
import fit.TypeAdapter;

public class ParseHelper {
	private Class<?> type;
	private Fixture fixture;
	public ParseHelper(Fixture fixture, Class<?> type){
		this.type=type;
		this.fixture=fixture;
	}
	private Object tryToConvert(Object value) throws Exception{
		try{
			return type.cast(value);
		}
		catch (ClassCastException cex){
				return parse(value.toString());
		}
	}
	private Object parseSymbol(String s) throws Exception{
		Object value=dbfit.util.SymbolUtil.getSymbol(s.substring(2).trim());
		if (value.getClass().equals(type))
			return value;
		// else try to convert
		try{
			return tryToConvert(value);
		} catch (Exception e){
			throw new UnsupportedOperationException(
						"Incompatible types between symbol and cell value: expected "+type +"; but symbol is "+value.getClass(),e);
		}
	}
	public Object parse(String s) throws Exception {
		if (s.startsWith("<<")){
			return parseSymbol(s);
		}
		String trim=s.trim();
		if (trim.toLowerCase().equals("null")) return null;
		if (this.type.equals(String.class) && Options.isFixedLengthStringParsing() &&
				trim.startsWith("'") && trim.endsWith("'")){
			return trim.substring(1,trim.length()-1);
		}
		TypeAdapter ta=TypeAdapter.adapterFor(this.type);
		ta.init(fixture, type);
	       
//		if (ta.getClass().equals(TypeAdapter.class)) return super.parse(s);
		return ta.parse(s);
	}
}
