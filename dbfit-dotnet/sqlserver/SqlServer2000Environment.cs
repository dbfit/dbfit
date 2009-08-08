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
using fit;

namespace dbfit
{
    /// <summary>
    /// Implementation of IDbEnviroment that works with SqlServer versions before 2005
    /// </summary>
    class SqlServer2000Environment : SqlServerEnvironment 
    {
        public override Dictionary<String, DbParameterAccessor> GetAllProcedureParameters(String procName)
        {
            if (procName.Contains("."))
            {
                String[] splitname = procName.Split('.');
                return ReadIntoParams(
                @"select parameter_name,data_type,character_maximum_length, parameter_mode,
                numeric_scale, numeric_precision 
                from information_schema.parameters
                where SPECIFIC_NAME=@objname
                and SPECIFIC_SCHEMA=@schemaname
                order by ordinal_position", splitname[0], splitname[1]
                );
            }
            else
            {
                return ReadIntoParams(
                @"select parameter_name,data_type,character_maximum_length, parameter_mode,
                numeric_scale, numeric_precision
                from information_schema.parameters
                where SPECIFIC_NAME=@objname
                and SPECIFIC_SCHEMA in ('dbo',user)
                order by ordinal_position", procName, null
               );
            }
        }
        public override Dictionary<String, DbParameterAccessor> GetAllColumns(String tableOrViewName)
        {
            if (tableOrViewName.Contains(".")){
                String[] splitname = tableOrViewName.Split('.');
                return ReadIntoParams(
                @"select column_name,data_type,character_maximum_length,'IN' as parameter_mode, 
                    numeric_scale, numeric_precision 
                    from information_schema.columns 
                    where table_name=@objname
                    and table_schema =@schemaname
                    order by ordinal_position", splitname[0], splitname[1]
                );
            }
            else {
                return ReadIntoParams(
                @"select column_name,data_type,character_maximum_length,'IN' as parameter_mode,
                    numeric_scale, numeric_precision 
                    from information_schema.columns 
                    where table_name=@objname
                    and table_schema in ('dbo',user)
                    order by ordinal_position", tableOrViewName,null
                );
            }
        }

        private  Dictionary<string, DbParameterAccessor> ReadIntoParams(String query, String objname, String schemaname)
        {
            objname = NameNormaliser.NormaliseName(objname);
            DbCommand dc = CurrentConnection.CreateCommand();
            dc.Transaction = CurrentTransaction;
            dc.CommandText = query;
            dc.CommandType = CommandType.Text;
            AddInput(dc, "@objname", objname);
            if (schemaname!=null)
                AddInput(dc, "@schemaname", NameNormaliser.NormaliseName(schemaname));
            DbDataReader reader = dc.ExecuteReader();
            Dictionary<String, DbParameterAccessor>
                allParams = new Dictionary<string, DbParameterAccessor>();
            int position=0;
            while (reader.Read())
            {

                String paramName = (reader.IsDBNull(0)) ? null : reader.GetString(0);
                String dataType = reader.GetString(1);
                int length = (reader.IsDBNull(2)) ? 0 : System.Convert.ToInt32(reader[2]);
                String direction = (reader.IsDBNull(3)) ? "IN" : reader.GetString(3);
                byte precision = 0;
                byte scale = 0;
                if (!reader.IsDBNull(4))                
                    scale=System.Convert.ToByte(reader[4]);
                if (!reader.IsDBNull(5))
                    precision = System.Convert.ToByte(reader[5]);

                SqlParameter dp = new SqlParameter();
                dp.Direction = GetParameterDirection(direction);
                if (!String.IsNullOrEmpty(paramName)) { 
						dp.ParameterName = paramName; dp.SourceColumn=paramName; 
				}
                else
                {
                    dp.Direction = ParameterDirection.ReturnValue;
                }
                dp.SqlDbType= GetDBType(dataType);
                if (precision > 0) dp.Precision = precision;
                if (scale > 0) dp.Scale = scale;

                if (length > 0)
                {
                    dp.Size = System.Convert.ToInt32(length);
                }
                else
                {
                    if (!ParameterDirection.Input.Equals(dp.Direction) || typeof(String).Equals(GetDotNetType(dataType)))
                        dp.Size = 4000;
                }
                allParams[NameNormaliser.NormaliseName(paramName)] =
                    new DbParameterAccessor(dp, GetDotNetType(dataType), position++, dataType);
            }
            reader.Close();
            if (allParams.Count == 0)
                throw new ApplicationException("Cannot read columns/parameters for object " + objname + " - check spelling or access privileges ");
            return allParams;
        }
        private static ParameterDirection GetParameterDirection(String direction)
        {
            if ("IN".Equals(direction)) return ParameterDirection.Input;
            if ("INOUT".Equals(direction)) return ParameterDirection.Output;
            else return ParameterDirection.Output;
        }
    }
}
