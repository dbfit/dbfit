package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.util.*;
import fit.Fixture;
import fit.Parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompareStoredQueries extends fit.Fixture {
	private String symbol1;
	private String symbol2;
	private DataTable dt1;
	private DataTable dt2;
	private String[] columnNames;
	private boolean[] keyProperties;
	
	public CompareStoredQueries() {
	}		
	public CompareStoredQueries(DBEnvironment environment, String symbol1, String symbol2){
		this.symbol1=symbol1;
		this.symbol2=symbol2;
	}

    private void initialiseDataTables(){
		if (symbol1==null||symbol2==null){
			if (args.length<2) throw new UnsupportedOperationException("No symbols specified to CompareStoreQueries constructor or argument list");
			symbol1=args[0];
			symbol2=args[1];			
		}
		dt1= SymbolUtil.getDataTable(symbol1);
		dt2= SymbolUtil.getDataTable(symbol2);
	}
	public void doTable(Parse table) {
		initialiseDataTables();		
		Parse lastRow=table.parts.more;
		if (lastRow==null){
			throw new Error("Query structure missing from second row");
		}
		loadRowStructure(lastRow);
		lastRow=processDataTable(dt1,dt2, lastRow, symbol2);
		
		List<DataRow> unproc=dt2.getUnprocessedRows();
		for (DataRow dr:unproc){
			lastRow=addRow(lastRow, dr, true, " missing from "+symbol1);
		}
	}
	private void loadRowStructure(Parse headerRow){
		Parse headerCell=headerRow.parts;
		int colNum=headerRow.parts.size();
		columnNames=new String[colNum];
		keyProperties=new boolean[colNum];
		for (int i=0; i<colNum; i++){
			String currentName=headerCell.text();
			if (currentName==null) throw new UnsupportedOperationException("Column "+i+" does not have a name");
			currentName=currentName.trim();
			if (currentName.length()==0) throw new UnsupportedOperationException("Column "+i+" does not have a name");
			columnNames[i]=NameNormaliser.normaliseName(currentName);
			keyProperties[i]=!currentName.endsWith("?");
			headerCell=headerCell.more;
		}
	}
	private Parse processDataTable(DataTable t1, DataTable t2, Parse lastScreenRow, String queryName){
		
		List<DataRow> unproc=t1.getUnprocessedRows();
		for (DataRow dr: unproc){
			Map<String,Object> matchingMask=new HashMap<String,Object>();
			for (int i=0; i<keyProperties.length; i++){
				if (keyProperties[i])
					matchingMask.put(columnNames[i], dr.get(columnNames[i]));
			}
			try {
					DataRow dr2=t2.findMatching(matchingMask);
					dr2.markProcessed();
					lastScreenRow=addRow(lastScreenRow,dr,dr2);
			}
			catch (NoMatchingRowFoundException nex){
				lastScreenRow=addRow(lastScreenRow,dr,true," missing from "+queryName);
			}
			dr.markProcessed();
		}
		return lastScreenRow;
	}
	private Parse addRow(Parse lastRow, DataRow dr, DataRow dr2){
		Parse newRow=new Parse("tr",null,null,null);
		lastRow.more=newRow;
		lastRow=newRow;
		try{
			String lval=dr.getStringValue(columnNames[0]);
			String rval=dr2.getStringValue(columnNames[0]);
			Parse firstCell=new Parse("td",lval,null,null);
			newRow.parts=firstCell;
			if (!lval.equals(rval)) {
				wrong(firstCell,rval);	
			}
			else{
				right(firstCell);
			}
			for (int i=1; i<columnNames.length; i++){
				lval=dr.getStringValue(columnNames[i]);
				rval=dr2.getStringValue(columnNames[i]);
				Parse nextCell=new Parse("td",
						lval,null,null);				
				firstCell.more=nextCell;
				firstCell=nextCell;
				if (!lval.equals(rval)) {
					wrong(firstCell,rval);	
				}
				else{
					right(firstCell);
				}
			}
		}
		catch (Exception e){
			exception(newRow, e);
		}
		return lastRow;
	}
	private Parse addRow(Parse lastRow, DataRow dr, boolean markAsError, String desc){
			Parse newRow=new Parse("tr",null,null,null);
			lastRow.more=newRow;
			lastRow=newRow;
			try{
				Parse firstCell=new Parse("td",
						dr.getStringValue(columnNames[0]),null,null);
				newRow.parts=firstCell;
				if (markAsError){
					firstCell.addToBody(Fixture.gray(desc));
					wrong(firstCell);	
				}
				for (int i=1; i<columnNames.length; i++){
					Parse nextCell=new Parse("td",
							dr.getStringValue(columnNames[i]),null,null);
					firstCell.more=nextCell;
					firstCell=nextCell;
				}
			}
			catch (Exception e){
				exception(newRow, e);
			}
			return lastRow;
		}
}
