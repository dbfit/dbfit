package org.dbfit.greenpepper.util;

import static com.greenpepper.annotation.Annotations.exception;
import static com.greenpepper.annotation.Annotations.right;
import static com.greenpepper.annotation.Annotations.wrong;

import com.greenpepper.Example;
import com.greenpepper.Statistics;
import com.greenpepper.TypeConversion;
import com.greenpepper.expectation.EqualExpectation;
import com.greenpepper.expectation.Expectation;

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

}
