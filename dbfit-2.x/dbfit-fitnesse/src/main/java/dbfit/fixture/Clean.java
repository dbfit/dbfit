package dbfit.fixture;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.dbfit.core.DBEnvironment;
import org.dbfit.core.DbEnvironmentFactory;
import fit.Parse;

public class Clean extends fit.ColumnFixture {
    private DBEnvironment environment;
	public Clean(DBEnvironment environment) {
		this.environment = environment;
	}
    public Clean()
    {
        this.environment = DbEnvironmentFactory.getDefaultEnvironment();
    }
    public String table;
    public String columnName;
    public BigDecimal[] ids;
    public String[] keys;
    public String where=null;
    private String getIDCSV()
    {
        StringBuilder sb = new StringBuilder();
        String comma = "";
        for (BigDecimal x :ids){
            sb.append(comma);
            sb.append(x.toString());
            comma = ", ";
        }
        return sb.toString();
    }
    private String getKeyCSV() 
    {
        StringBuilder sb = new StringBuilder();
        String comma = "";
        for (String x : keys)
        {
            sb.append(comma);
            sb.append("'");
            sb.append(x.toString());
            sb.append("'");
            comma = ", ";
        }
        return sb.toString();
    }

    private boolean hadRowOperation=false;		
	public boolean clean() throws SQLException{
		String s=  "Delete from " + table +(where!=null?(" where "+where):"");
		environment.createStatementWithBoundFixtureSymbols(s).execute();
		return true;
	}
    public boolean DeleteRowsForIDs() throws SQLException
    {
        String s=    "Delete from " + table + " where "+columnName +" in ("
            + getIDCSV()+") "+(where != null ? " and " + where : "");
		environment.createStatementWithBoundFixtureSymbols(s).execute();
		hadRowOperation=true;
		return true;
    }
    public boolean DeleteRowsForKeys() throws SQLException
    {
    	String s=   "Delete from " + table + " where " + columnName + " in ("
            + getKeyCSV() + ") " + (where != null ? " and " + where : "");
    	environment.createStatementWithBoundFixtureSymbols(s).execute();
    	hadRowOperation=true;
    	return true;
    }
	public void doRow(Parse row) {
		hadRowOperation = false;
		super.doRow(row);
		if (!hadRowOperation) {
			try{
				clean();
			}
			catch (SQLException sqle){
				exception(row,sqle);	
			}
		}
	}
}
