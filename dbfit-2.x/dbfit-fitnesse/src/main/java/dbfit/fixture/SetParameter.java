package dbfit.fixture;

import fit.Parse;

/** 
 * simple wrapper for fixture symbol value setting, 
 * which takes care of NULL keyword and symbol loading
 * 
 */
public class SetParameter extends fit.Fixture{
	public static void setParameter(String name, String value){
		if (value == null || "null".equals(value.toString().toLowerCase())) {
			dbfit.util.SymbolUtil.setSymbol(name, null);
		} else if (value != null && value.toString().startsWith("<<")) {
			String varname = value.toString().substring(2);
			if (!name.equals(varname)) {
				dbfit.util.SymbolUtil.setSymbol(name, dbfit.util.SymbolUtil.getSymbol(varname));
			}
		} else
			dbfit.util.SymbolUtil.setSymbol(name, value);
	}
	@Override
	public void doTable(Parse table) {
		if (args.length!=2) throw new UnsupportedOperationException("Set parameter requires two arguments");
		setParameter(args[0], args[1]);
	}
}
