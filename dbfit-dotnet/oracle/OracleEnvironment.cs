/// Copyright (C) Gojko Adzic 2006-2008 http://gojko.net
/// Released under GNU GPL 2.0
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Data.OracleClient;
using System.Text.RegularExpressions;
using System.Xml;
using System.Text;
using dbfit.util;
namespace dbfit {  
/// <summary>
/// Implementation of IDbEnvironment that uses Microsoft's ADO.NET driver for Oracle
/// </summary>
    
    public class OracleEnvironment : AbstractDbEnvironment {
        protected override String GetConnectionString(String dataSource, String username, String password)
        {
			return String.Format("Data Source={0}; User ID={1}; Password={2}",dataSource,username,password);
		}
        protected override String GetConnectionString(String dataSource, String username, String password, String databaseName)
        {
            return String.Format("Data Source={0}/{3}; User ID={1}; Password={2}", dataSource, username, password,databaseName);
        }
		private Regex paramNames = new Regex(":([A-Za-z0-9_]+)");
        protected override Regex ParamNameRegex { get { return paramNames; } }

        private static DbProviderFactory dbp = DbProviderFactories.GetFactory("System.Data.OracleClient");
        public override DbProviderFactory DbProviderFactory
        {
            get { return dbp; }
		}
        public override Dictionary<String, DbParameterAccessor> GetAllProcedureParameters(String procName)
        {
			String[] qualifiers = NameNormaliser.NormaliseName(procName).Split('.');
            String cols = " argument_name, data_type, data_length, IN_OUT, sequence ";
			String qry = @" select " + cols + " from all_arguments where data_level=0 and ";
			if (qualifiers.Length == 3) {
				qry += " owner=:0 and package_name=:1 and object_name=:2 ";
			} else if (qualifiers.Length == 2) {
				qry += @" ((owner=:0 and package_name is null and object_name=:1) or 
					(owner=user and package_name=:0 and object_name=:1))";
			} else {
				qry += @" 
					(owner=user and package_name is null and object_name=:0)";
			}
			// map to public synonyms also
			if (qualifiers.Length<3){
				qry+=@" union all
						select " +cols+@" from all_arguments, all_synonyms			
						where data_level=0 and all_synonyms.owner='PUBLIC' and all_arguments.owner=table_owner and ";
				if (qualifiers.Length==2){  // package
					qry+=" package_name=table_name and synonym_name=:0 and object_name=:1 ";
				}						
				else {		
					qry+=" package_name is null and object_name=table_name and synonym_name=:0 ";
				}
			}
            qry+=" order by sequence ";
			//Console.WriteLine(qry);
			Dictionary<String, DbParameterAccessor> res=ReadIntoParams(qualifiers, qry);
            if (res.Count == 0) throw new ApplicationException("Cannot read list of parameters for " + procName + " - check spelling and access privileges");
            return res;
		}
        public override Dictionary<String, DbParameterAccessor> GetAllColumns(String tableOrViewName)
        {
			String[] qualifiers = NameNormaliser.NormaliseName(tableOrViewName).Split('.');
			String qry = @" select column_name, data_type, data_length, 
				'IN' as direction, column_id from all_tab_columns where ";
			if (qualifiers.Length == 2) {
				qry += " owner=:0 and table_name=:1 ";
			} else {
				qry += @" 
					(owner=user and table_name=:0)";
			}
            qry += " order by column_id ";
            Dictionary<String, DbParameterAccessor> res = ReadIntoParams(qualifiers, qry);
            if (res.Count == 0) throw new ApplicationException("Cannot read list of columns for " + tableOrViewName + " - check spelling and access privileges");
            return res;
		}

		private Dictionary<string, DbParameterAccessor> ReadIntoParams(String[] queryParameters, String query) {
			DbCommand dc = CurrentConnection.CreateCommand();
			dc.Transaction = CurrentTransaction;
			dc.CommandText = query;
			dc.CommandType = CommandType.Text;
			for (int i = 0; i < queryParameters.Length; i++) {
				AddInput(dc, ":" + i, queryParameters[i].ToUpper());
			}
			DbDataReader reader = dc.ExecuteReader();
			Dictionary<String, DbParameterAccessor>
				allParams = new Dictionary<string, DbParameterAccessor>();
            int position = 0;
			while (reader.Read()) {

				String paramName = (reader.IsDBNull(0)) ? null : reader.GetString(0);
				String dataType = reader.GetString(1);
				int length = (reader.IsDBNull(2)) ? 0 : reader.GetInt32(2);
				String direction = reader.GetString(3);
				OracleParameter dp = new OracleParameter();				
				dp.Direction = GetParameterDirection(direction);
				if (paramName != null) { 
					dp.ParameterName = paramName; dp.SourceColumn=paramName; 
				}
				else {
					dp.Direction = ParameterDirection.ReturnValue;
				}
								
				dp.OracleType = GetDBType(dataType);
				if (length > 0) {
					dp.Size = length;

				} else {
					if (!ParameterDirection.Input.Equals(dp.Direction) || typeof(String).Equals(GetDotNetType(dataType)))
						dp.Size = 4000;
				}
				allParams[NameNormaliser.NormaliseName(paramName)] =
                    new DbParameterAccessor(dp, GetDotNetType(dataType), position++, dataType);
			}
			return allParams;
		}
		private static string[] StringTypes = new string[] { "VARCHAR", "VARCHAR2", "NVARCHAR2", "CHAR", "NCHAR", "ROWID", "CLOB", "NCLOB" };
		private static string[] DecimalTypes = new string[] { "BINARY_INTEGER","NUMBER","FLOAT" };
		private static string[] DateTypes = new string[] { "TIMESTAMP", "DATE" };
		private static string[] RefCursorTypes = new string[] { "REF" };

		private static string NormaliseTypeName(string dataType) {
			dataType = dataType.ToUpper().Trim();
			int idx = dataType.IndexOf(" ");
			if (idx >= 0) dataType = dataType.Substring(0, idx);
			idx = dataType.IndexOf("(");
			if (idx >= 0) dataType = dataType.Substring(0, idx);
			return dataType;
		}
		private static OracleType GetDBType(String dataType) {
			//todo:strip everything from first blank
			dataType = NormaliseTypeName(dataType);

			if (Array.IndexOf(StringTypes, dataType) >= 0) return OracleType.VarChar;
			if (Array.IndexOf(DecimalTypes, dataType) >= 0) return OracleType.Number;
			if (Array.IndexOf(DateTypes, dataType) >= 0) return OracleType.DateTime;
			if (Array.IndexOf(RefCursorTypes, dataType) >= 0) return OracleType.Cursor;
			throw new NotSupportedException("Type " + dataType + " is not supported");
		}
		private static Type GetDotNetType(String dataType) {
			dataType = NormaliseTypeName(dataType);
			if (Array.IndexOf(StringTypes, dataType) >= 0) return typeof(string);
			if (Array.IndexOf(DecimalTypes, dataType) >= 0) return typeof(decimal);
			if (Array.IndexOf(DateTypes, dataType) >= 0) return typeof(DateTime);
			if (Array.IndexOf(RefCursorTypes, dataType) >= 0) return typeof(DataTable);

			throw new NotSupportedException("Type " + dataType + " is not supported");
		}
		private static ParameterDirection GetParameterDirection(String direction) {
			if ("IN".Equals(direction)) return ParameterDirection.Input;
			if ("OUT".Equals(direction)) return ParameterDirection.Output;
			if ("IN/OUT".Equals(direction)) return ParameterDirection.InputOutput;
			//todo return val
			throw new NotSupportedException("Direction " + direction + " is not supported");
		}
        public override String BuildInsertCommand(String tableName, DbParameterAccessor[] accessors)
        {
            StringBuilder sb = new StringBuilder("insert into ");
            sb.Append(tableName).Append("(");
            String comma = "";
            String retComma = "";

            StringBuilder values = new StringBuilder();
            StringBuilder retNames = new StringBuilder();
            StringBuilder retValues = new StringBuilder();

            foreach (DbParameterAccessor accessor in accessors)
            {
                if (!accessor.IsBoundToCheckOperation)
                {
                    sb.Append(comma);
                    values.Append(comma);
                    sb.Append(accessor.DbParameter.SourceColumn);
                    values.Append(":").Append(accessor.DbParameter.ParameterName);
                    comma = ",";
                }
                else
                {
                    retNames.Append(retComma);
                    retValues.Append(retComma);
						  retNames.Append(accessor.DbParameter.SourceColumn);
                    retValues.Append(":").Append(accessor.DbParameter.ParameterName);
                    retComma = ",";
                }
            }
            sb.Append(") values (");
            sb.Append(values);
            sb.Append(")");
            if (retValues.Length > 0)
            {
                sb.Append(" returning ").Append(retNames).Append(" into ").Append(retValues);
            }
            return sb.ToString();
        }
        public override int GetExceptionCode(Exception dbException)
        {
            if (dbException is System.Data.OracleClient.OracleException)
                return ((System.Data.OracleClient.OracleException)dbException).Code;
            else if (dbException is System.Data.Common.DbException)
                return ((System.Data.Common.DbException)dbException).ErrorCode;
            else return 0;
        }
        public override String ParameterPrefix{
				get { return ":"; }
		}
        public override bool SupportsReturnOnInsert { get { return true; } }
        public override String IdentitySelectStatement { get { throw new ApplicationException("Oracle supports return on insert"); } }

	}
}
