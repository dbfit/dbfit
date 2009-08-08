using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Text;
using System.Text.RegularExpressions;

using fit;
using dbfit.util;
namespace dbfit.fixture {
	public class Insert:fit.Fixture{
		
		
		IDbEnvironment dbEnvironment;
		DbCommand command;
		String tableName;

        // array of parameter accessors for the input command
        // this contains only those accessors that really go into input (i.e. not the primary key
        // column if the database does not support return on insert. for databases that support
        // return on insert, this will be the same as columnAccessors
		DbParameterAccessor[] accessors;
        Accessor[] columnAccessors;
        bool[] isOutputColumn; 
        public Insert()
        {
            this.dbEnvironment = DbEnvironmentFactory.DefaultEnvironment;
        }
        public Insert(IDbEnvironment dbEnvironment)
        {
            this.dbEnvironment=dbEnvironment;
        }
		public Insert(IDbEnvironment dbEnvironment, String tableName) {
            this.tableName= tableName;
			this.dbEnvironment = dbEnvironment;
			
		}		
		public override void DoRows(Parse rows) {
            if (String.IsNullOrEmpty(tableName) && Args.Length > 0)
            {
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
            String insert = dbEnvironment.BuildInsertCommand(tableName, accessors);
            //Console.WriteLine(insert);
            command = dbEnvironment.CreateCommand(insert,
                    CommandType.Text);
            foreach (DbParameterAccessor accessor in accessors) {
                  command.Parameters.Add(accessor.DbParameter);
            }
		}

      
		private void RunRow(Parse row) {
			Parse cell = row.Parts;
			//first set input params
            for (int col = 0; col < columnAccessors.Length; col++)
            {
                Accessor accessor = columnAccessors[col];
                ICellHandler cellHandler = CellOperation.GetHandler(this, cell, accessor.ParameterType);
                if (!isOutputColumn[col])
                {
                    cellHandler.HandleInput(this, cell, accessor);
                }
                cell = cell.More;
            }
            command.ExecuteNonQuery();
			cell = row.Parts;
			//next evaluate output params
            for (int col=0; col<columnAccessors.Length; col++)
            {
			    Accessor accessor=columnAccessors[col];
                ICellHandler cellHandler = CellOperation.GetHandler(this,cell, accessor.ParameterType);
				if (isOutputColumn[col]) {
					cellHandler.HandleCheck(this, cell, accessor);
				}
				cell = cell.More;
			}
		}
		private static Regex checkIsImpliedByRegex = new Regex("(\\?|!|\\(\\))$");

        // this method will initialise accessors array from the parameters that
        // really go into the insert command and columnAccessors for all columns
		private void InitParameters(Parse headerCells) {			
			Dictionary<String,DbParameterAccessor> allParams=
				dbEnvironment.GetAllColumns(tableName);
			columnAccessors = new Accessor[headerCells.Size];
            isOutputColumn = new bool[headerCells.Size];
            List<DbParameterAccessor> paramAccessors=new List<DbParameterAccessor>();
			for (int i = 0; headerCells != null; i++, headerCells = headerCells.More) {
				String paramName= NameNormaliser.NormaliseName(headerCells.Text);
                DbParameterAccessor currentColumn;
                try
                {
                    currentColumn = allParams[paramName];
                }
                catch (System.Collections.Generic.KeyNotFoundException)
                {
                    Wrong(headerCells);
                    throw new ApplicationException("Cannot find column " + paramName);
                }
                isOutputColumn[i] = checkIsImpliedByRegex.IsMatch(headerCells.Text);
                currentColumn.IsBoundToCheckOperation = isOutputColumn[i];
                columnAccessors[i] = currentColumn;
                if (isOutputColumn[i])
                {
                    if (dbEnvironment.SupportsReturnOnInsert)
                    {
                        currentColumn.DbParameter.Direction = ParameterDirection.Output;
                        paramAccessors.Add(currentColumn);
                    }
                    else // don't add to paramAccessors
                    {
                        columnAccessors[i] = new dbfit.util.IdRetrievalAccessor(dbEnvironment, currentColumn.DotNetType);
                    }
                }
                else // not output
                {
                    currentColumn.DbParameter.Direction = ParameterDirection.Input;
                    paramAccessors.Add(currentColumn);
                }
            }
            accessors = paramAccessors.ToArray();
		}
	}
}
