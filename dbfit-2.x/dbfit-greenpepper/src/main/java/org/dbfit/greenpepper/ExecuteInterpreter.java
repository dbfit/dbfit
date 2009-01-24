package org.dbfit.greenpepper;

import static com.greenpepper.annotation.Annotations.exception;
import static com.greenpepper.annotation.Annotations.wrong;

import java.sql.PreparedStatement;

import org.dbfit.core.DbObject;
import org.dbfit.greenpepper.util.DbAccessorCellHelper;
import org.dbfit.greenpepper.util.GreenPepperTestHost;

import com.greenpepper.Example;
import com.greenpepper.ExecutionContext;
import com.greenpepper.Interpreter;
import com.greenpepper.Specification;
import com.greenpepper.Statistics;
import com.greenpepper.interpreter.HeaderForm;
import com.greenpepper.interpreter.column.Column;
import com.greenpepper.reflect.Fixture;

import dbfit.util.DbParameterAccessor;

/** 
 * this interpreter will execute a given statement for 
 * each of the examples in the specification, first populating
 * all inputs, then running the statement, finally checking all the outputs
 * and finally clearing all statement parameters. this interpreter handles
 * statements, stored procedures and inserts depending on the implementation
 * of the DbObject passed in 
 */
public class ExecuteInterpreter implements Interpreter{
    private final DbObject targetTable;    
    protected Statistics stats;
    private DbParameterAccessor[] accessors;
    private PreparedStatement statement;
    private Column[] columns;
    public ExecuteInterpreter( Fixture fixture )
    {
        if (fixture.getTarget() instanceof DbObject){
        	this.targetTable=(DbObject) fixture.getTarget();
        }
        else
        	throw new UnsupportedOperationException("This interpreter does not support "+fixture);
    }
	
    public void interpret( Specification specification )
    {
        stats = new Statistics();
        Example table = specification.nextExample();
        accessors = getDbAccessors( table );
        GreenPepperTestHost.getInstance().copyToExecutionContext(specification);
        try{
	        if (accessors.length==0) {
	        	targetTable.buildPreparedStatement(new DbParameterAccessor[0]).execute();		
	        }
	        else{
	            columns=getColumns(table, accessors, specification);
	        	statement=targetTable.buildPreparedStatement(accessors);        
	        	for (Example row = table.at( 0, 2 ); row != null; row = row.nextSibling())
		        {
		            runRow( row );
		        }
	        }
        }
        catch (Exception e){
        	table.annotate(exception(e));
        	stats.exception();
        }
        specification.exampleDone( stats );
        GreenPepperTestHost.getInstance().copyFromExecutionContext(specification);        
    }
 
    private DbParameterAccessor[] getDbAccessors( Example table ){
        Example headers = table.at( 0, 1, 0 );
        if (headers == null) return new DbParameterAccessor[0];

        DbParameterAccessor[] columns = new DbParameterAccessor[headers.remainings()];
        for (int i = 0; i < headers.remainings(); i++)
        {
            columns[i] = getDbParameterAccessor( headers.at( i ) );
        }
        return columns;
    }
    private Column[] getColumns(Example table, DbParameterAccessor[] accessors, ExecutionContext specification) throws Exception{
        Example headers = table.at( 0, 1, 0 );
        if (headers == null) return new Column[0];
        Column[] columns = new Column[headers.remainings()];
        for (int i = 0; i < headers.remainings(); i++)
        {

        	columns[i]=DbAccessorCellHelper.getColumn(headers.at(i).getContent(),accessors[i]);
        	columns[i].bindTo(specification);
        }
        return columns;

    }
    private DbParameterAccessor getDbParameterAccessor( Example header )
    {
        try
        {
        	String headerContent=header.getContent();
        	HeaderForm hf=HeaderForm.parse(headerContent);
        	DbParameterAccessor accessor=targetTable.getDbParameterAccessor(headerContent, 
        			hf.isGiven()?DbParameterAccessor.INPUT:DbParameterAccessor.OUTPUT);
        	if (accessor==null) { header.annotate(wrong()); return null;}        	
        	return accessor;
        }
        catch (Exception e)
        {
            header.annotate( exception( e ) );
            stats.exception();
            return null;
        }
    }
	private void runRow(Example row)  throws Exception{
		statement.clearParameters();
		if (!row.hasChild()) return;
		Example cells = row.firstChild();
		for(int column=0; column<accessors.length; column++){
			if (accessors[column].getDirection()==DbParameterAccessor.INPUT) {
				columns[column].doCell(cells.at( column ));
			}
		} 
		statement.execute();
		for(int column=0; column<accessors.length; column++){
			if (accessors[column].getDirection()==DbParameterAccessor.OUTPUT||
					accessors[column].getDirection()==DbParameterAccessor.RETURN_VALUE) {
				columns[column].doCell(cells.at( column ));
			}
		}							
	}
}
