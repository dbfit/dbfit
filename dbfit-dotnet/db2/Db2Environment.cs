
/// Copyright (C) Gojko Adzic 2006-2008 http://gojko.net
/// Released under GNU GPL 2.0

using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using IBM.Data.DB2;
using System.Text.RegularExpressions;
using System.Xml;
using System.Text;
using fit;
using dbfit.util;
namespace dbfit
{
    /// <summary>
    /// IDBEnvironment implementation that connects DBFit to IBM DB2 Express
    /// Tested using DB2 Express 9.5. This database does not support calling
    /// stored functions directly.
    /// 
    /// </summary>
    public class DB2Environment: AbstractDbEnvironment
    {
        protected override String GetConnectionString(String dataSource, String username, String password, String databaseName)
        {
            return String.Format("database={3};server={0};userid={1};password={2}", dataSource, username, password, databaseName);
        }

        protected override String GetConnectionString(String dataSource, String username, String password)
        {
            return String.Format("server={0};userid={1};password={2}", dataSource, username, password);
        }
        private static DbProviderFactory dbp = DbProviderFactories.GetFactory("IBM.Data.DB2");
        private Regex paramNames = new Regex("@([A-Za-z0-9_]*)");
        protected override Regex ParamNameRegex { get { return paramNames; } }

        public override DbProviderFactory DbProviderFactory
        {
            get { return dbp;}
        }
        public override Dictionary<String, DbParameterAccessor> GetAllProcedureParameters(String procName)
        {
            String[] qualifiers = NameNormaliser.NormaliseName(procName).Split('.');
            String qry = " select parmname as column_name, typename as data_type, length, "
                    + "	rowtype as direction, ordinal from SYSIBM.SYSroutinePARMS  where ";
            if (qualifiers.Length== 2)
            {
                qry += " lower(routineschema)=@0 and lower(routinename)=@1 ";
            }
            else
            {
                qry += " (lower(routinename)=@0)";
            }
            qry += " order by ordinal";
            Dictionary<String, DbParameterAccessor> res = ReadIntoParams(qualifiers, qry,"");
            if (res.Count == 0) throw new ApplicationException("Cannot read list of parameters for " + procName + " - check spelling and access privileges");
            return res;
        }
        public override Dictionary<String, DbParameterAccessor> GetAllColumns(String tableOrViewName)
        {
            String[] qualifiers = NameNormaliser.NormaliseName(tableOrViewName).Split('.');
            String qry = " select colname as column_name, typename as data_type, length, "
                    + "	'P' as direction from syscat.columns where ";
            if (qualifiers.Length == 2)
            {
                qry += " lower(tabschema)=@0 and lower(tabname)=@1 ";
            }
            else
            {
                qry += " (lower(tabname)=@0)";
            }
            qry += " order by colname";
            Dictionary<String, DbParameterAccessor> res = ReadIntoParams(qualifiers, qry,"@");
            if (res.Count == 0) throw new ApplicationException("Cannot read list of parameters for " + tableOrViewName + " - check spelling and access privileges");
            return res;
        }

        private Dictionary<string, DbParameterAccessor> ReadIntoParams(String[] queryParameters, String query, String addPrefix)
        {
            DbCommand dc = CurrentConnection.CreateCommand();
            dc.Transaction = CurrentTransaction;
            dc.CommandText = query;
            dc.CommandType = CommandType.Text;
            for (int i = 0; i < queryParameters.Length; i++)
            {
                AddInput(dc, i.ToString(), queryParameters[i].ToLower());
            }
            DbDataReader reader = dc.ExecuteReader();
            Dictionary<String, DbParameterAccessor>
                allParams = new Dictionary<string, DbParameterAccessor>();
            int position = 0;
            while (reader.Read())
            {
                String paramName = (reader.IsDBNull(0)) ? null : reader.GetString(0).ToUpper();
                String dataType = reader.GetString(1);
                int length = (reader.IsDBNull(2)) ? 0 : reader.GetInt32(2);
                String direction = reader.GetString(3);
                DB2Parameter dp = new DB2Parameter();
                dp.Direction = GetParameterDirection(direction);
                if (paramName != null)
                {
                    dp.ParameterName = addPrefix+paramName; dp.SourceColumn = paramName;
                }
                else
                {
                    dp.Direction = ParameterDirection.ReturnValue;
                }

                dp.DB2Type= GetDBType(dataType);
                if (length > 0)
                {
                    dp.Size = length;

                }
                else
                {
                    if (!ParameterDirection.Input.Equals(dp.Direction) || typeof(String).Equals(GetDotNetType(dataType)))
                        dp.Size = 4000;
                }
                allParams[NameNormaliser.NormaliseName(paramName)] =
                    new DbParameterAccessor(dp, GetDotNetType(dataType), position++, dataType);
            }
            return allParams;
        }
        private static string[] StringTypes = new string[] { "VARCHAR", "CHAR", "CHARACTER", "GRAPHIC", "VARGRAPHIC" };
        private static string[] DecimalTypes = new string[] { "DECIMAL","DEC","FLOAT","DOUBLE" };
        private static string[] DateTypes = new string[] { "DATE"};
        private static string[] TimestampTypes = new string[] { "TIMESTAMP" };
        private static string[] IntTypes = new string[] { "SMALLINT", "INT", "INTEGER"};
        private static string[] LongTypes = new string[] { "BIGINT" };

        private static string NormaliseTypeName(string dataType)
        {
            return dataType.ToUpper().Trim();
        }
        protected static DB2Type GetDBType(String dataType)
        {
            //todo:strip everything from first blank
            dataType = NormaliseTypeName(dataType);

            if (Array.IndexOf(StringTypes, dataType) >= 0) return DB2Type.VarChar;
            if (Array.IndexOf(DecimalTypes, dataType) >= 0) return DB2Type.Decimal;
            if (Array.IndexOf(DateTypes, dataType) >= 0) return DB2Type.Date;
            if (Array.IndexOf(IntTypes, dataType) >= 0) return DB2Type.Integer;
            if (Array.IndexOf(LongTypes, dataType) >= 0) return DB2Type.BigInt;
            if (Array.IndexOf(TimestampTypes, dataType) >= 0) return DB2Type.Timestamp;
            throw new NotSupportedException("Type " + dataType + " is not supported");
        }
        protected static Type GetDotNetType(String dataType)
        {
            dataType = NormaliseTypeName(dataType);
            if (Array.IndexOf(StringTypes, dataType) >= 0) return typeof(string);
            if (Array.IndexOf(DecimalTypes, dataType) >= 0) return typeof(decimal);
            if (Array.IndexOf(IntTypes, dataType) >= 0) return typeof(Int32);
            if (Array.IndexOf(LongTypes, dataType) >= 0) return typeof(Int64);

            if (Array.IndexOf(DateTypes, dataType) >= 0) return typeof(DateTime);
            if (Array.IndexOf(TimestampTypes, dataType) >= 0) return typeof(DateTime);
            throw new NotSupportedException("Type " + dataType + " is not supported");
        }
        private static ParameterDirection GetParameterDirection(String direction)
        {
            if ("P".Equals(direction)) return ParameterDirection.Input;
            if ("O".Equals(direction)) return ParameterDirection.Output;
            if ("B".Equals(direction)) return ParameterDirection.InputOutput;
            if ("C".Equals(direction)) return ParameterDirection.ReturnValue;
            //todo return val
            throw new ApplicationException("Direction " + direction + " is not supported");
        }
        public override bool SupportsReturnOnInsert { get { return false; } }
        public override String IdentitySelectStatement 
        { 
            get {
                return "select IDENTITY_VAL_LOCAL() from sysibm.sysdummy1"; 
            } 
        }

        /** DB2 behaves a bit strange with parameters -- it looks like 
         * @NAME must be declared as parameter name, not just NAME. for that reason,
         * the prefix that gets automatically added to parameter names while building commands
         * is blank
         */
        public override string ParameterPrefix
        {
            get { return ""; }
        }
        protected override void AddInput(DbCommand dbCommand, String name, Object value)
        {
            DbParameter dbParameter = dbCommand.CreateParameter();
            dbParameter.Direction = ParameterDirection.Input;
            dbParameter.ParameterName = "@"+name;
            dbParameter.Value = (value == null ? DBNull.Value : value);
            dbCommand.Parameters.Add(dbParameter);
        }

    }
}
