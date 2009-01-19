package org.dbfit.greenpepper.util;

import static com.greenpepper.annotation.Annotations.exception;
import static com.greenpepper.annotation.Annotations.right;
import static com.greenpepper.annotation.Annotations.wrong;

import com.greenpepper.Example;
import com.greenpepper.Statistics;
import com.greenpepper.TypeConversion;
import com.greenpepper.expectation.EqualExpectation;
import com.greenpepper.expectation.Expectation;
import com.greenpepper.interpreter.HeaderForm;
import com.greenpepper.interpreter.column.Column;
import com.greenpepper.interpreter.column.ExpectedColumn;
import com.greenpepper.interpreter.column.GivenColumn;
import com.greenpepper.interpreter.column.RecalledColumn;
import com.greenpepper.interpreter.column.SavedColumn;
import com.greenpepper.reflect.Message;

import dbfit.util.DbParameterAccessor;

public class DbAccessorCellHelper {
    public static Statistics doInputCell(DbParameterAccessor accessor, Example cell )
    {
    	Statistics stats=new Statistics();
        try
        {
        	accessor.set(TypeConversion.parse(cell.getContent(),accessor.getJavaType()));
        }
        catch (Exception e)
        {
            cell.annotate( exception( e ) );
            stats.exception();
        }
        return stats;
    }
    public static Statistics doOutputCell(DbParameterAccessor accessor, Example cell )
    {
    	Statistics stats=new Statistics();
        try
        {
        	// todo handle symbols!
        	Object expected=TypeConversion.parse(cell.getContent(),accessor.getJavaType());
        	Expectation ex=new EqualExpectation(expected);
        	Object actual=accessor.get();
        	if (ex.meets(actual)) cell.annotate(right());
        	else cell.annotate(wrong(ex,actual));        	
        }
        catch (Exception e)
        {
            cell.annotate( exception( e ) );
            stats.exception();
        }
        return stats;
    }
	public static Column getColumn(final String headerText,
			final DbParameterAccessor dbParameterAccessor) throws Exception {
    	
		HeaderForm header= HeaderForm.parse(headerText);
    	if (header.isExpected()) return new ExpectedColumn(new Message(){
    		@Override
    		public int getArity() {
    			return 0;
    		}
    		@Override
    		public Object send(String... args) throws Exception {
    			return dbParameterAccessor.get();
    		}
    	});
    	if (header.isGiven()) return new GivenColumn(new Message(){
    		@Override
    		public int getArity() {
    			return 0;
    		}
    		@Override
    		public Object send(String... args) throws Exception {
    			dbParameterAccessor.set(TypeConversion.parse(args[0],dbParameterAccessor.getJavaType()));
    			return null;
    		}
    	});
    	if (header.isSaved()) return new SavedColumn(new Message(){
    		@Override
    		public int getArity() {
    			return 0;
    		}
    		@Override
    		public Object send(String... args) throws Exception {
    			return dbParameterAccessor.get();
    		}
    	});
    	if (header.isRecalled()) return new RecalledColumn(new Message(){
    		@Override
    		public int getArity() {
    			return 0;
    		}
    		@Override
    		public Object send(String... args) throws Exception {
    			dbParameterAccessor.set(TypeConversion.parse(args[0],dbParameterAccessor.getJavaType()));
    			return null;
    		}
    	}); 
    	throw new UnsupportedOperationException("Unsupported header scheme "+headerText);
	}

}
