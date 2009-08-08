using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;

using fit;

namespace dbfit {
	public class DbParameterAccessor : AbstractAccessor {
		private DbParameter dbp;
        private String actualSqlType;
        private Type dotNetType;
		private String dbFieldName;// this can be used when parameter name in SQL should differ from the data field name
		private bool isBoundToCheckOperation = false;
        private int position;
        public int Position
        {
            get { return position; }
        }
        public String ActualSqlType
        {
            get { return actualSqlType; }
        }
        public Type DotNetType
        {
			get { return dotNetType;}
		}
        public DbParameter DbParameter
		{
			get { return dbp; }
			internal set { dbp = value; }
		}
		public String DbFieldName {
			get { return dbFieldName; } 
		}
        public bool IsBoundToCheckOperation
		{
			get { return isBoundToCheckOperation; }
            internal set { isBoundToCheckOperation = value; }
		}
		public static DbParameterAccessor Clone(DbParameterAccessor dbacc, IDbEnvironment environment)
		{
			DbParameter cloneP=environment.DbProviderFactory.CreateParameter();
			cloneP.ParameterName=dbacc.DbParameter.ParameterName;
			cloneP.DbType= dbacc.DbParameter.DbType;
			cloneP.Direction= dbacc.DbParameter.Direction;
			cloneP.Size= dbacc.DbParameter.Size;
			cloneP.SourceColumn = dbacc.DbParameter.SourceColumn;
			cloneP.Value=dbacc.DbParameter.Value;
			DbParameterAccessor clone=new DbParameterAccessor(cloneP, dbacc.dotNetType,dbacc.position, dbacc.actualSqlType);
			clone.dbFieldName=dbacc.dbFieldName;
			clone.isBoundToCheckOperation=dbacc.isBoundToCheckOperation;            
            return clone;
		}
        public static DbParameterAccessor CloneWithSameParameter(DbParameterAccessor dbacc)
        {
            DbParameterAccessor clone = new DbParameterAccessor(dbacc.DbParameter, dbacc.dotNetType, dbacc.position, dbacc.actualSqlType);
            clone.dbFieldName = dbacc.dbFieldName;
            clone.isBoundToCheckOperation = dbacc.isBoundToCheckOperation;
            return clone;
        }

		public DbParameterAccessor(DbParameter dbp, Type dotNetType, int position, String actualSqlType)
			: base(dotNetType) {
			this.dbp = dbp;
			this.dotNetType = dotNetType;
			this.dbFieldName=dbp.ParameterName;
            this.position = position;
            this.actualSqlType = actualSqlType;
		}
		public override object Get(Fixture fixture) {
			if (ParameterDirection.Input.Equals(dbp.Direction))
				throw new NotSupportedException("Cannot use input parameters as output values. Please remove the question mark after " + dbp.ParameterName);
			if (typeof(DataTable).Equals(dotNetType) && (dbp.Value is DbDataReader)) {
					DataTable dt=new DataTable();
					dt.Load((DbDataReader) dbp.Value);
					return dt;
			}
			//Console.Write("Reading value of "+dbp.DbType+" " +dbp.ParameterName + " = " + dbp.Value+"\n");
			return dbp.Value;
		}
		public override void Set(Fixture fixture, object value) {
			if (ParameterDirection.Input.Equals(dbp.Direction)
				||
				ParameterDirection.InputOutput.Equals(dbp.Direction))
				dbp.Value = value==null?DBNull.Value:value;
			else
				throw new NotSupportedException("Cannot use output parameters as input values. Did you forget a question mark after " + dbp.ParameterName);

		}
	}
}
