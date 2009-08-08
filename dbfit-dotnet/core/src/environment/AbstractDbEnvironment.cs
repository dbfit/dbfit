/// Copyright (C) Gojko Adzic 2006-2008 http://gojko.net
/// Released under GNU GPL 2.0

using System;
using System.Collections.Generic;
using System.Data.Common;
using System.Data;
using System.Text.RegularExpressions;
using System.Text;
namespace dbfit
{
    /// <summary>
    /// Utility class to simplify development of IDbEnvironment implementations. This class
    /// has implementations for methods that will be common across most databases. It also provides
    /// default accessors for the current connection and transaction.
    /// </summary>
    public abstract class AbstractDbEnvironment : IDbEnvironment
    {
        /// <summary>
        /// optional prefix symbol that is added to parameter names in Sql commands.
        /// </summary>
        public abstract String ParameterPrefix { get; }

        #region default implementations for IDbEnvironment methods and properties

        private DbConnection _currentConnection;
        private DbTransaction _currentTransaction;

        public DbConnection CurrentConnection
        {
            get
            {
                return _currentConnection;
            }
            protected set
            {
                _currentConnection = value;
            }
        }
        public DbTransaction CurrentTransaction
        {
            get
            {
                return _currentTransaction;
            }
            protected set
            {
                _currentTransaction = value;
            }

        }

        public  virtual void Connect(String dataSource, String username, String password, String database)
        {
            Connect(GetConnectionString(dataSource, username, password, database));
        }
        public  virtual void Connect(String dataSource, String username, String password)
        {
            Connect(GetConnectionString(dataSource, username, password));
        }
        public  virtual void Connect(String connectionString)
        {
            CurrentConnection = DbProviderFactory.CreateConnection();
            CurrentConnection.ConnectionString = connectionString;
            CurrentConnection.Open();
            CurrentTransaction = CurrentConnection.BeginTransaction();
        }
        public  virtual void ConnectUsingFile(String connectionPropertiesFile)
        {
            DbConnectionProperties dbp = DbConnectionProperties.CreateFromFile(connectionPropertiesFile);
            if (dbp.FullConnectionString != null) Connect(dbp.FullConnectionString);
            else if (dbp.DbName != null) Connect(dbp.Service, dbp.Username, dbp.Password, dbp.DbName);
            else Connect(dbp.Service, dbp.Username, dbp.Password);
        }
        protected virtual void AddInput(DbCommand dbCommand, String name, Object value)
        {
            DbParameter dbParameter = dbCommand.CreateParameter();
            dbParameter.Direction = ParameterDirection.Input;
            dbParameter.ParameterName = name;
            dbParameter.Value = (value == null ? DBNull.Value : value);
            dbCommand.Parameters.Add(dbParameter);
        }
        public  virtual DbCommand CreateCommand(string statement, CommandType commandType)
        {
            if (CurrentConnection == null) throw new ApplicationException("Not connected to database");

            DbCommand dc = CurrentConnection.CreateCommand();
            dc.CommandText = statement.Replace("\r", " ").Replace("\n", " ");
            dc.CommandType = commandType;
            dc.Transaction = CurrentTransaction;
            return dc;
        }
        public void BindFixtureSymbols(DbCommand dc)
        {
            foreach (String paramName in ExtractParamNames(dc.CommandText))
            {
                AddInput(dc, paramName, fit.Fixture.Recall(paramName));
            }
        }
        public void CloseConnection()
        {
            if (CurrentTransaction != null)
            {
                CurrentTransaction.Rollback();
            }
            if (CurrentConnection != null)
            {
                CurrentConnection.Close();
                CurrentConnection = null;
                CurrentTransaction = null;
            }
        }
        public void Commit()
        {
            CurrentTransaction.Commit();
            CurrentTransaction = CurrentConnection.BeginTransaction();
        }
        public void Rollback()
        {
            CurrentTransaction.Rollback();
            CurrentTransaction = CurrentConnection.BeginTransaction();
        }

        public  virtual String BuildUpdateCommand(String tableName, DbParameterAccessor[] updateAccessors, DbParameterAccessor[] selectAccessors)
        {
            if (updateAccessors.Length == 0)
            {
                throw new ApplicationException("must have at least one field to update. Have you forgotten = after the column name?");
            }
            System.Text.StringBuilder s = new System.Text.StringBuilder("update ").Append(tableName).Append(" set ");
            for (int i = 0; i < updateAccessors.Length; i++)
            {
                if (i > 0) s.Append(", ");
                s.Append(updateAccessors[i].DbParameter.SourceColumn).Append("=");
                s.Append(this.ParameterPrefix).Append(updateAccessors[i].DbParameter.ParameterName);
            }
            s.Append(" where ");
            for (int i = 0; i < selectAccessors.Length; i++)
            {
                if (i > 0) s.Append(" and ");
                s.Append(selectAccessors[i].DbParameter.SourceColumn).Append("=");
                s.Append(this.ParameterPrefix).Append(selectAccessors[i].DbParameter.ParameterName);
            }
            return s.ToString();
        }
        public  virtual string[] ExtractParamNames(string commandText)
        {
            //dotnet2 does not support sets, so a set is simmulated with a hashmap
            Dictionary<string, string> parameters = new Dictionary<string, string>();
            MatchCollection mc = ParamNameRegex.Matches(commandText);
            for (int i = 0; i < mc.Count; i++) parameters[mc[i].Groups[1].Value] = mc[i].Groups[1].Value;
            string[] arr = new string[parameters.Keys.Count];
            parameters.Keys.CopyTo(arr, 0);
            return arr;
        }

        public virtual int GetExceptionCode(Exception dbException)
        {
            if (dbException is System.Data.Common.DbException)
            {
                System.Console.WriteLine("DBEXCEPTION:" + ((System.Data.Common.DbException)dbException).ErrorCode);
                return ((System.Data.Common.DbException)dbException).ErrorCode;
            }
            else
            {
                System.Console.WriteLine("EXCEPTION:" + dbException.GetType().ToString());
                return 0;
            }
        }
        public virtual String BuildInsertCommand(String tableName, DbParameterAccessor[] accessors)
        {
            StringBuilder sb = new StringBuilder("insert into ");
            sb.Append(tableName).Append("(");
            String comma = "";
            StringBuilder values = new StringBuilder();
            foreach (DbParameterAccessor accessor in accessors)
            {
                sb.Append(comma);
                values.Append(comma);
                sb.Append(accessor.DbParameter.SourceColumn);
                values.Append(ParameterPrefix).Append(accessor.DbParameter.ParameterName);
                comma = ",";
            }
            sb.Append(") values (");
            sb.Append(values);
            sb.Append(")");
            return sb.ToString();
        }
        #endregion

        #region inherited methods from IDbEnvironment that have no default implementation

        protected abstract String GetConnectionString(String dataSource, String username, String password);
        protected abstract String GetConnectionString(String dataSource, String username, String password, String database);
        protected abstract Regex ParamNameRegex { get; }
        public abstract DbProviderFactory DbProviderFactory { get ; }
        public abstract Dictionary<string, DbParameterAccessor> GetAllProcedureParameters(string procName);
        public abstract Dictionary<string, DbParameterAccessor> GetAllColumns(string tableOrViewName);

        public abstract bool SupportsReturnOnInsert { get;}
        public abstract String IdentitySelectStatement { get;}

#endregion

    }

}
