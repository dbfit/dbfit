package dbfit.fixture;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.dbfit.core.DbObject;

import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessorTypeAdapter;
import dbfit.util.SymbolAccessQueryBinding;
import dbfit.util.SymbolAccessSetBinding;
import fit.Binding;
import fit.Fixture;
import fit.Parse;

public abstract class DbObjectExecutionFixture extends Fixture{
	private DbParameterAccessor[] accessors;
	private Binding[] columnBindings;
	private PreparedStatement statement;
	private DbObject dbObject; // intentionally private, subclasses should extend getTargetObject
	
	protected abstract DbObject getTargetDbObject() throws SQLException;
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
			exception(rows.parts, e);
		}
	}
	private static boolean isOutput(String name){
		return name.endsWith("?");
	}
	private static DbParameterAccessor[] EMPTY=new DbParameterAccessor[0];
    private DbParameterAccessor[] getAccessors( Parse headerCells) throws SQLException{
        if (headerCells == null) return EMPTY;
		DbParameterAccessor accessors[]=new DbParameterAccessor[headerCells.size()];
        for (int i = 0; headerCells != null; i++, headerCells = headerCells.more) {
			String name=headerCells.text();
			accessors[i]=dbObject.getDbParameterAccessor(name, 
        			isOutput(name)?DbParameterAccessor.OUTPUT:DbParameterAccessor.INPUT);
			if (accessors[i]==null) {
					exception (headerCells,new IllegalArgumentException("Parameter/column "+name+" not found"));
					return null;
			}			
        }
        return accessors;
    }
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
    
	private void runRow(Parse row)  throws Throwable{
		statement.clearParameters();
		Parse cell = row.parts;
		//first set input params
		for(int column=0; column<accessors.length; column++,	cell = cell.more){
			if (accessors[column].getDirection()==DbParameterAccessor.INPUT) {
				columnBindings[column].doCell(this, cell);
			}
		} 
		statement.execute();
		cell = row.parts;
		//next evaluate output params
		for(int column=0; column<accessors.length; column++, cell = cell.more){
			if (accessors[column].getDirection()==DbParameterAccessor.OUTPUT||
					accessors[column].getDirection()==DbParameterAccessor.RETURN_VALUE) {
				columnBindings[column].doCell(this, cell);
			}
		}							
	}

}