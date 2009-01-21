package org.dbfit.greenpepper.util;

import com.greenpepper.TypeConversion;
import com.greenpepper.interpreter.HeaderForm;
import com.greenpepper.interpreter.column.Column;
import com.greenpepper.interpreter.column.ExpectedColumn;
import com.greenpepper.interpreter.column.GivenColumn;
import com.greenpepper.interpreter.column.RecalledColumn;
import com.greenpepper.interpreter.column.SavedColumn;
import com.greenpepper.reflect.Message;

import dbfit.util.DbParameterAccessor;

public class DbAccessorCellHelper {
	public static Column getColumn(final String headerText,
			final DbParameterAccessor dbParameterAccessor) throws Exception {
    	
		HeaderForm header= HeaderForm.parse(headerText);
    	if (header.isExpected()) return new ExpectedColumn(new Message(){
    		public int getArity() {
    			return 0;
    		}
    		public Object send(String... args) throws Exception {
    			return dbParameterAccessor.get();
    		}
    	});
    	if (header.isGiven()) return new GivenColumn(new Message(){
    		public int getArity() {
    			return 0;
    		}
    		public Object send(String... args) throws Exception {
    			try{
	    			if (args[0]==null || args[0].equals("null")){
	    				dbParameterAccessor.set(null);
	    			}
	    			else{
	    				dbParameterAccessor.set(TypeConversion.parse(args[0],dbParameterAccessor.getJavaType()));
	    			}
    			}
    			catch (Exception e){
    				e.printStackTrace();
    				throw e;
    			}
    			return null;
    		}
    	});
    	if (header.isSaved()) return new SavedColumn(new Message(){
    		public int getArity() {
    			return 0;
    		}
    		public Object send(String... args) throws Exception {
    			return dbParameterAccessor.get();
    		}
    	});
    	if (header.isRecalled()) return new RecalledColumn(new Message(){
    		public int getArity() {
    			return 0;
    		}
    		public Object send(String... args) throws Exception {
    			dbParameterAccessor.set(TypeConversion.parse(args[0],dbParameterAccessor.getJavaType()));
    			return null;
    		}
    	}); 
    	throw new UnsupportedOperationException("Unsupported header scheme "+headerText);
	}

}
