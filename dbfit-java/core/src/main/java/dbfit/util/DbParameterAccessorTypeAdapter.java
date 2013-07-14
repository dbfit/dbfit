package dbfit.util;

import fit.Fixture;
import fit.TypeAdapter;

import java.lang.reflect.InvocationTargetException;

public class DbParameterAccessorTypeAdapter extends TypeAdapter {	
	private ParameterOrColumn parameterAccessor;
	public DbParameterAccessorTypeAdapter(ParameterOrColumn accessor,Fixture f){
		this.fixture=f;
		this.type=accessor.getJavaType();
		this.parameterAccessor=accessor;		
	}
	@Override
	public Object get() throws IllegalAccessException,
			InvocationTargetException {
		return parameterAccessor.get();
	}
	@Override
	public void set(Object value) throws Exception {
		parameterAccessor.set(value);
	}
	@Override
	public Object parse(String s) throws Exception {
		return new ParseHelper(this.fixture,this.type).parse(s);
	}
}
