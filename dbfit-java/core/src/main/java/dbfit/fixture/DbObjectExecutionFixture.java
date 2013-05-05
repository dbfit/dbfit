package dbfit.fixture;

import dbfit.api.DbObject;
import dbfit.util.*;
import fit.Binding;
import fit.Fixture;
import fit.Parse;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;

import static dbfit.util.DbParameterAccessor.Direction.*;

/** this class handles all cases where a statement should be executed for each row with 
 * given inputs and verifying optional outputs or exceptions. it also handles a special case 
 * when just a single statement is executed without binding parameters to columns. Examples are
 * - Inserting data into tables/views
 * - Executing statements
 * - Updates
 * - Stored procedures/functions
 * 
 * the object under test is defined by overriding getTargetObject. Unfortunately, because of the way FIT
 * instantiates fixtures, passing in an object using a constructor and aggregation simply doesn't do the trick
 * so users have to extend this fixture.
 * 
 *
 */
public abstract class DbObjectExecutionFixture extends Fixture{
	private DbParameterAccessor[] accessors=new DbParameterAccessor[0];
	private Binding[] columnBindings;
	private PreparedStatement statement;
	private DbObject dbObject; // intentionally private, subclasses should extend getTargetObject

	/** override this method to control whether an exception is expected or not. By default, expects no exception to happen */
	protected ExpectedBehaviour getExpectedBehaviour(){
		return ExpectedBehaviour.NO_EXCEPTION;
	}
	
	/** override this method and supply the expected exception number, if one is expected */
	protected int getExpectedErrorCode(){
		return 0;
	}
	
	/** override this method and supply the dbObject implementation that will be executed for each row */
	protected abstract DbObject getTargetDbObject() throws SQLException;
	/** 
	 * executes the target dbObject for all rows of the table. if no rows are specified, executes
	 * the target object only once
	 */
	public void doRows(Parse rows) {
		try{
			dbObject=getTargetDbObject();
			if (dbObject==null) throw new Error("DB Object not specified!"); 
			if (rows==null){//single statement, no args
	        	dbObject.buildPreparedStatement(accessors).execute();		
	        	return;
			}
			accessors=getAccessors(rows.parts);
			if (accessors==null) return ;// error reading args
			columnBindings=getColumnBindings(rows.parts, accessors);	    	
	    	statement=dbObject.buildPreparedStatement(accessors);        
			Parse row = rows;
			while ((row = row.more) != null) {
				runRow(row);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			if (rows==null) throw new Error(e);
			exception(rows.parts, e);
		}
	}
	/** does the column name map to an output argument */
	private static boolean isOutput(String name){
		return name.endsWith("?");
	}
	private static DbParameterAccessor[] EMPTY=new DbParameterAccessor[0];
	/** initialise db parameters for the dbObject based on table header cells */
    private DbParameterAccessor[] getAccessors( Parse headerCells) throws SQLException{
        if (headerCells == null) return EMPTY;
		DbParameterAccessor accessors[]=new DbParameterAccessor[headerCells.size()];
        for (int i = 0; headerCells != null; i++, headerCells = headerCells.more) {
			String name=headerCells.text();
			accessors[i]=dbObject.getDbParameterAccessor(name, 
        			isOutput(name)? OUTPUT:INPUT);
			if (accessors[i]==null) {
					exception (headerCells,new IllegalArgumentException("Parameter/column "+name+" not found"));
					return null;
			}			
        }
        return accessors;
    }
    /** bind db accessors to columns based on the text in the header */
    private Binding[] getColumnBindings(Parse headerCells, DbParameterAccessor[] accessors) throws Exception{
        if (headerCells == null) return new Binding[0];
		Binding[] columns=new Binding[headerCells.size()];
		for (int i = 0; headerCells != null; i++, headerCells = headerCells.more) {
			String name=headerCells.text();
			if (isOutput(name)){
				columns[i]=new SymbolAccessQueryBinding();
			}
			else{
				columns[i]=new SymbolAccessSetBinding();
			}
			columns[i].adapter=new DbParameterAccessorTypeAdapter(accessors[i],this);
        }
        return columns;		
    }
    
    /** execute a single row */
	private void runRow(Parse row)  throws Throwable{
		statement.clearParameters();
		Parse cell = row.parts;
		//first set input params
		for(int column=0; column<accessors.length; column++,	cell = cell.more){
			if (accessors[column].getDirection().equals(INPUT)) {
				columnBindings[column].doCell(this, cell);
			}
		} 
		if (getExpectedBehaviour()==ExpectedBehaviour.NO_EXCEPTION){
			executeStatementAndEvaluateOutputs(row);					
		}
		else if (getExpectedBehaviour()==ExpectedBehaviour.ANY_EXCEPTION||
				getExpectedBehaviour()==ExpectedBehaviour.SPECIFIC_EXCEPTION){
			executeStatementExpectingException(row);
		}
		else throw new UnsupportedOperationException("Got unsupported expected behaviour enum value");
		
	}
	private void executeStatementExpectingException(Parse row) throws Exception{
	    String savepointName = "eee" + this.hashCode();
	    if (savepointName.length()>10) savepointName=savepointName.substring(1,9);
	    Savepoint savepoint = null;
		try{
			savepoint = statement.getConnection().setSavepoint(savepointName);
			statement.execute();
			wrong(row);
		}
		catch (SQLException e){
			e.printStackTrace();
			// all good, exception expected
			if (getExpectedBehaviour()==ExpectedBehaviour.ANY_EXCEPTION){
				right(row);
			}
			else{
				int realError=dbObject.getDbEnvironment().getExceptionCode(e);
				if (realError==getExpectedErrorCode())
					right(row);
				else{
					wrong(row);
					row.parts.addToBody(fit.Fixture.gray(" got error code "+realError));
				}
			}
		}
	    if (savepoint != null) {
	          statement.getConnection().rollback(savepoint);
		}

	}
	
	
	private void executeStatementAndEvaluateOutputs(Parse row)
			throws SQLException, Throwable {
		statement.execute();
		Parse cells = row.parts;
		for(int column=0; column<accessors.length; column++, cells = cells.more){
			if (accessors[column].getDirection().equals(OUTPUT)||
					accessors[column].getDirection().equals(RETURN_VALUE)) {
				columnBindings[column].doCell(this, cells);
			}
		}
	}

}