using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Text;
using System.Text.RegularExpressions;
using dbfit.util;
using fit;

namespace dbfit.fixture {
	public class Update:fit.Fixture {		
		IDbEnvironment dbEnvironment;
		DbCommand command;
		String tableName;
		DbParameterAccessor[] updateAccessors;
		DbParameterAccessor[] selectAccessors;
		DbParameterAccessor[] columnBindings;
		
		public Update(){
			this.dbEnvironment = DbEnvironmentFactory.DefaultEnvironment;
		}
		public Update(IDbEnvironment dbEnvironment){
			this.dbEnvironment=dbEnvironment;
		}
		public Update(IDbEnvironment dbEnvironment, String tableName){
			this.tableName= tableName;
			this.dbEnvironment = dbEnvironment;
		}		
		public override void DoRows(Parse rows) {
         if (String.IsNullOrEmpty(tableName) && Args.Length > 0){
             tableName = Args[0];
         }
			else if (tableName == null) {
					tableName=rows.Parts.Text;
					rows = rows.More;
			}			
			InitParameters(rows.Parts);
			InitCommand();
			Parse row = rows;
			while ((row = row.More) != null) {				
				RunRow(row);
			}			
		}
		private void InitCommand() {
			String ctext=dbEnvironment.BuildUpdateCommand(tableName,updateAccessors,selectAccessors);
			//Console.WriteLine(ctext);
			command = dbEnvironment.CreateCommand(ctext,CommandType.Text);
         foreach (DbParameterAccessor accessor in updateAccessors) {
                command.Parameters.Add(accessor.DbParameter);
         }
			foreach (DbParameterAccessor accessor in selectAccessors)
			{
				command.Parameters.Add(accessor.DbParameter);
			}
		}

      
		private void RunRow(Parse row) {
			Parse cell = row.Parts;
			//first set input params
			foreach (DbParameterAccessor accessor in columnBindings) {
				ICellHandler cellHandler = CellOperation.GetHandler(this,cell, accessor.ParameterType);
				cellHandler.HandleInput(this, cell, accessor);
				cell = cell.More;
			}
			command.ExecuteNonQuery();
		}		
		private void InitParameters(Parse headerCells) {			
			Dictionary<String,DbParameterAccessor> allParams=
				dbEnvironment.GetAllColumns(tableName);

			columnBindings = new DbParameterAccessor[headerCells.Size];
			IList<DbParameterAccessor> selectAccList=new List<DbParameterAccessor>();
			IList<DbParameterAccessor> updateAccList = new List<DbParameterAccessor>();
			for (int i = 0; headerCells != null; i++, headerCells = headerCells.More)
			{
				String paramName= NameNormaliser.NormaliseName(headerCells.Text);
                try
                {
                    DbParameterAccessor acc = allParams[paramName];
                    acc.DbParameter.Direction = ParameterDirection.Input;
                    // allow same column to be used in both sides: 
                    // remap update parameters to u_paramname and select to s_paramname
                    acc = DbParameterAccessor.Clone(acc, dbEnvironment);
                    if (headerCells.Text.EndsWith("="))
                    {
                        acc.DbParameter.ParameterName = acc.DbParameter.ParameterName+"_u";
                        updateAccList.Add(acc);
                    }
                    else
                    {
                        acc.DbParameter.ParameterName = acc.DbParameter.ParameterName+"_s";
                        selectAccList.Add(acc);
                    }
                    columnBindings[i] = acc;
                }
                catch (System.Collections.Generic.KeyNotFoundException)
                {
                    Wrong(headerCells);
                    throw new ApplicationException("Cannot find column for " + paramName);
                }
			}
			selectAccessors=new DbParameterAccessor[selectAccList.Count];
			selectAccList.CopyTo(selectAccessors,0);
			updateAccessors = new DbParameterAccessor[updateAccList.Count];
			updateAccList.CopyTo(updateAccessors, 0);			
		}
	}
}
