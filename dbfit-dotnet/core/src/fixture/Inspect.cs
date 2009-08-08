using System;
using System.Collections.Generic;
using System.Text;
using dbfit.util;
using dbfit;
using fit;
using System.Data.Common;
using System.Data;
namespace dbfit.fixture
{
    public class Inspect:fit.Fixture
    {
        private IDbEnvironment environment;
        private String objectName;
	    private String mode;
	    public static String MODE_PROCEDURE="PROCEDURE";
	    public static String MODE_TABLE="TABLE";
	    public static String MODE_QUERY="QUERY";
	
        public Inspect()
        {
            this.environment = DbEnvironmentFactory.DefaultEnvironment;
        }
	    public Inspect(IDbEnvironment dbEnvironment, String mode, String objName) {
		    this.objectName= objName;
		    this.mode=mode;
		    this.environment = dbEnvironment;		
	    }
	    public override void DoTable(Parse table) {
		    if (objectName==null) objectName=Args[0];
		    try{
			    if (MODE_PROCEDURE.Equals(mode))
				    InspectProcedure(table);
                else if (MODE_TABLE.Equals(mode))
				    InspectTable(table);
                else if (MODE_QUERY.Equals(mode))
				    InspectQuery(table);
			    else throw new Exception("Unknown inspect mode "+mode);
		    }
		    catch (Exception e){
			    Exception(table.Parts.Parts,e);
		    }
	    }
	    private void InspectTable(Parse table) {
		    Dictionary<String, DbParameterAccessor> allParams=
			    environment.GetAllColumns(objectName);
		    if (allParams.Count==0){
			    throw new ApplicationException("Cannot retrieve list of columns for table or view "+objectName + " - check spelling and access rights");
		    }
		    addRowWithParamNames(table,allParams);
	    }
	    private void InspectProcedure(Parse table) {
		    Dictionary<String, DbParameterAccessor> allParams=
			    environment.GetAllProcedureParameters(objectName);
		    if (allParams.Count==0){
			    throw new ApplicationException("Cannot retrieve list of parameters for procedure "+objectName + " - check spelling and access rights");
		    }
		    addRowWithParamNames(table,allParams);
	    }
	    private void InspectQuery(Parse table) {
            DbCommand dc = environment.CreateCommand(objectName, CommandType.Text);
            environment.BindFixtureSymbols(dc);

            DbDataAdapter oap = environment.DbProviderFactory.CreateDataAdapter();
            oap.SelectCommand = dc;
            DataSet ds = new DataSet();
            oap.Fill(ds);
            dc.Dispose();
            DataTable dtbl=ds.Tables[0];

		    Parse newRow=getHeaderFromRS(dtbl);
		    table.Parts.More=newRow;
		    foreach (DataRow dr in dtbl.Rows){
                newRow.More = getDataRow(dr);
			    newRow=newRow.More;
		    }
		    
	    }
	    private Parse getDataRow(DataRow rs) {
		    Parse newRow=new Parse("tr",null,null,null);
		    Parse prevCell=null;
		    for (int i=0; i<rs.Table.Columns.Count; i++){
			    Object value=DataColumnAccessor.GetValue(rs,rs.Table.Columns[i]);
			    Parse cell=new Parse("td",Fixture.Gray(value==null?"null":value.ToString()),null,null);
			    if (prevCell==null) 
					    newRow.Parts=cell;
			    else
					    prevCell.More=cell;
			    prevCell=cell;			
		    }
		    return newRow;
    		
	    }
	    private Parse getHeaderFromRS(DataTable rs) {
		    Parse newRow=new Parse("tr",null,null,null);
		    Parse prevCell=null;
		    for (int i=0; i<rs.Columns.Count; i++){
			    Parse cell=new Parse("td",Fixture.Gray(rs.Columns[i].ColumnName),null,null);
			    if (prevCell==null) 
					    newRow.Parts=cell;
			    else
					    prevCell.More=cell;
			    prevCell=cell;			
		    }
		    return newRow;
	    }

        private void addRowWithParamNames(Parse table, Dictionary<String,DbParameterAccessor> procparams){
		    Parse newRow=new Parse("tr",null,null,null);
		    table.Parts.More=newRow;
		    Parse prevCell=null;
		    String[] orderedNames=new String[procparams.Count];		
		    foreach(String s  in  procparams.Keys){
			    orderedNames[procparams[s].Position]=s;
		    }
		    for(int i=0; i<orderedNames.Length; i++){
			    String name=orderedNames[i];
                if (procparams[name].DbParameter.Direction!=System.Data.ParameterDirection.Input ) 
                        name = name + "?";
			    Parse cell=new Parse("td",Fixture.Gray(name),null,null);
			    if (prevCell==null) 
					    newRow.Parts=cell;
			    else
					    prevCell.More=cell;
			    prevCell=cell;
		    }
	    }

    }
}
