/// Copyright (C) Gojko Adzic 2006-2008 http://gojko.net
/// Released under GNU GPL 2.0

using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Data.SqlClient;
using System.Text.RegularExpressions;
using System.Xml;
using System.Text;
using dbfit.util;

namespace dbfit
{
    /// <summary>
    /// Implementation of IDbEnvironment that works with SqlServer 2005 and newer versions
    /// </summary>
    public class SqlServerEnvironment : AbstractDbEnvironment
    {
        private static readonly int MAX_STRING_SIZE = 4000;
        protected override String GetConnectionString(String dataSource, String username, String password, String databaseName)
        {
            return String.Format("data source={0};user id={1};password={2};database={3};", dataSource, username, password, databaseName);
        }

        protected override String GetConnectionString(String dataSource, String username, String password)
        {
            return String.Format("Data Source={0}; User ID={1}; Password={2}", dataSource, username, password);
        }
        private static DbProviderFactory dbp = DbProviderFactories.GetFactory("System.Data.SqlClient");
        private Regex paramNames = new Regex("@([A-Za-z0-9_]*)");
        protected override Regex ParamNameRegex { get { return paramNames;}}

        public override DbProviderFactory DbProviderFactory
        {
            get { return dbp; }
        }
        public override Dictionary<String, DbParameterAccessor> GetAllProcedureParameters(String procName)
        {
            
            return ReadIntoParams(procName,
               @"select p.[name], TYPE_NAME(p.system_type_id) as [Type], 
			   p.max_length, p.is_output, p.is_cursor_ref, p.precision, p.scale from sys.parameters p 
				where p.object_id = OBJECT_ID(@objname) order by parameter_id"
               );
        }
        public override Dictionary<String, DbParameterAccessor> GetAllColumns(String tableOrViewName)
        {

            return ReadIntoParams(tableOrViewName,
            @"select c.[name], TYPE_NAME(c.system_type_id) as [Type], c.max_length, 
            0 As is_output, 0 As is_cursor_ref, c.precision, c.scale
            from sys.columns c
            where c.object_id = OBJECT_ID(@objname)
            order by column_id
            " );
        }

        private  Dictionary<string, DbParameterAccessor> ReadIntoParams(String objname, String query)
        {
            if (objname.Contains("."))
            {
                String[] schemaAndName = objname.Split(new char[] { '.' }, 2);
                objname = "[" + schemaAndName[0] + "].[" + schemaAndName[1] + "]";
            }
            else
            {
                objname = "[" + NameNormaliser.NormaliseName(objname) + "]";
            }
            DbCommand dc = CurrentConnection.CreateCommand();
            dc.Transaction = CurrentTransaction;
            dc.CommandText = query;
            dc.CommandType = CommandType.Text;
            AddInput(dc, "@objname", objname);
            DbDataReader reader = dc.ExecuteReader();
            Dictionary<String, DbParameterAccessor>
                allParams = new Dictionary<string, DbParameterAccessor>();
            int position=0;
            while (reader.Read())
            {

                String paramName = (reader.IsDBNull(0)) ? null : reader.GetString(0);
                String dataType = reader.GetString(1);
                int length = (reader.IsDBNull(2)) ? 0 : System.Convert.ToInt32(reader[2]);
                int isOutput = (reader.IsDBNull(3)) ? 0 : System.Convert.ToInt32(reader[3]);
                byte precision =  System.Convert.ToByte(reader[5]);
                byte scale = System.Convert.ToByte(reader[6]);

                SqlParameter dp = new SqlParameter();
                dp.Direction = GetParameterDirection(isOutput);
                if (!String.IsNullOrEmpty(paramName)) { 
						dp.ParameterName = paramName; dp.SourceColumn=paramName; 
				}
                else
                {
                    dp.Direction = ParameterDirection.ReturnValue;
                }
                dp.SqlDbType= GetDBType(dataType);
                String typeName = NormaliseTypeName(dataType);
                if (precision > 0) dp.Precision = precision;         
                if (scale > 0) dp.Scale = scale;
                if ("NTEXT".Equals(typeName)||("TEXT".Equals(typeName)))
                    dp.Size=MAX_STRING_SIZE;
                else if (length > 0)
                {
                    dp.Size = System.Convert.ToInt32(length);
                }
                else
                {
                    if (!ParameterDirection.Input.Equals(dp.Direction) || 
                        typeof(String).Equals(GetDotNetType(dataType)))
                        dp.Size = MAX_STRING_SIZE;
                }
                allParams[NameNormaliser.NormaliseName(paramName)] =
                    new DbParameterAccessor(dp, GetDotNetType(dataType), position++, dataType);
            }
            reader.Close();
            if (allParams.Count == 0)
                throw new ApplicationException("Cannot read columns/parameters for object " + objname + " - check spelling or access privileges ");
            return allParams;
        }
        private static string[] StringTypes = new string[] { "VARCHAR", "NVARCHAR", "CHAR", "NCHAR","TEXT","NTEXT","XML"};
        private static string[] DecimalTypes = new string[] { "DECIMAL", "NUMERIC", "MONEY", "SMALLMONEY" };
        private static string[] DateTypes = new string[] { "SMALLDATETIME","DATETIME","TIMESTAMP" };
        private static string[] RefCursorTypes = new string[] { "REF" };
        private static string[] Int32Types=new string[] {"INT"};
        private static string[] Int16Types = new string[] { "TINYINT", "SMALLINT"};
        private static string[] Int64Types = new string[] { "BIGINT"};

        private static string[] BooleanTypes = new string[] { "BIT" };
		private static string[] BinaryTypes=new string[] {"BINARY","VARBINARY","IMAGE"};
        private static string[] GuidTypes = new string[] { "UNIQUEIDENTIFIER" };
        private static string[] VariantTypes = new string[] { "SQL_VARIANT" };
        private static string[] FloatTypes = new String[] { "REAL", "FLOAT" };
        private static string NormaliseTypeName(string dataType)
        {
            dataType = dataType.ToUpper().Trim();
            int idx = dataType.IndexOf(" ");
            if (idx >= 0) dataType = dataType.Substring(0, idx);
            idx = dataType.IndexOf("(");
            if (idx >= 0) dataType = dataType.Substring(0, idx);
            return dataType;
            
        }
        protected static SqlDbType GetDBType(String dataType)
        { 
            //todo:strip everything from first blank
            dataType = NormaliseTypeName(dataType);

            if (Array.IndexOf(StringTypes, dataType) >= 0) return SqlDbType.VarChar;
            if (Array.IndexOf(DecimalTypes, dataType) >= 0) return SqlDbType.Decimal;
            if (Array.IndexOf(DateTypes, dataType) >= 0) return SqlDbType.DateTime;
            if (Array.IndexOf(Int32Types, dataType) >= 0) return SqlDbType.Int;
            if (Array.IndexOf(Int16Types, dataType) >= 0) return SqlDbType.Int;
            if (Array.IndexOf(Int64Types, dataType) >= 0) return SqlDbType.Int;
            if (Array.IndexOf(BooleanTypes, dataType) >= 0) return SqlDbType.Bit;
			if (Array.IndexOf(BinaryTypes,dataType)>=0) return SqlDbType.VarBinary;
            //if (Array.IndexOf(RefCursorTypes, dataType) >= 0) return OracleType.Cursor;
            if (Array.IndexOf(GuidTypes, dataType) >= 0) return SqlDbType.UniqueIdentifier;
            if (Array.IndexOf(VariantTypes, dataType) >= 0) return SqlDbType.Variant;
            if (Array.IndexOf(FloatTypes, dataType) >= 0) return SqlDbType.Float;

            throw new NotSupportedException("Type " + dataType + " is not supported");
        }
        protected static Type GetDotNetType(String dataType)
        {
            dataType = NormaliseTypeName(dataType);
            if (Array.IndexOf(StringTypes, dataType) >= 0) return typeof(string);
            if (Array.IndexOf(DecimalTypes, dataType) >= 0) return typeof(decimal);
            if (Array.IndexOf(Int32Types, dataType) >= 0) return typeof(Int32);
            if (Array.IndexOf(Int16Types, dataType) >= 0) return typeof(Int16);
            if (Array.IndexOf(Int64Types, dataType) >= 0) return typeof(Int64);
            if (Array.IndexOf(DateTypes, dataType) >= 0) return typeof(DateTime);
            if (Array.IndexOf(RefCursorTypes, dataType) >= 0) return typeof(DataTable);
            if (Array.IndexOf(BooleanTypes, dataType) >= 0) return typeof(bool);
			if (Array.IndexOf(BinaryTypes, dataType) >= 0) return typeof(byte[]);
            if (Array.IndexOf(GuidTypes, dataType) >= 0) return typeof(System.Guid);
            if (Array.IndexOf(VariantTypes, dataType) >= 0) return typeof(string);
            if (Array.IndexOf(FloatTypes, dataType) >= 0) return typeof(double);

            throw new NotSupportedException("Type " + dataType + " is not supported");
        }
        private static ParameterDirection GetParameterDirection(int isOutput)
        {
            if (isOutput==1) return ParameterDirection.Output;
            else return ParameterDirection.Input;
        }
        public override bool SupportsReturnOnInsert { get { return false; } }
        public override String IdentitySelectStatement { get { return "select @@identity"; } }

        public override int GetExceptionCode(Exception dbException)
        {

            if (dbException is System.Data.SqlClient.SqlException)
            {
                Console.WriteLine("SQL Exception " + ((System.Data.SqlClient.SqlException)dbException).Number);
                return ((System.Data.SqlClient.SqlException)dbException).Number;
            }
            else return base.GetExceptionCode(dbException);
        }
		 public override string ParameterPrefix
		 {
			 get { return "@";}
		 }
    }
}
