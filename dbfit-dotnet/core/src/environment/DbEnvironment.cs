/// Copyright (C) Gojko Adzic 2006-2008 http://gojko.net
/// Released under GNU GPL 2.0
using System;
using System.Collections.Generic;
using System.Data.Common;
using System.Data;
using System.Text.RegularExpressions;
using System.Text;
namespace dbfit {
    /// <summary>
    /// IDbEnvironment provides a common interface for database-specific meta-data retrieval
    /// and operations. IDbEnvironment instances encapculate all database-specific functionality,
    /// so that DbFit fixtures can be database-neutral. IDbEnvironment instances are also
    /// responsible for transaction management.
    /// </summary>
	public interface IDbEnvironment {
        /// <summary>
        /// Retrieves an instance of DbProviderFactory class that can be used
        /// to create database-specific implementations of database parameters,
        /// data-set readers etc
        /// </summary>
        DbProviderFactory DbProviderFactory { get ; }
        /// <summary>
        /// A flag to signal whether the database supports output parameters on insert commands,
        /// or whether the auto-generated identity values have to be retrieved with a separate
        /// statement after insert.
        /// </summary>
	    bool SupportsReturnOnInsert { get ;}
        /// <summary>
        /// If the database requires an additional statement to fetch auto-generated identity values,
        /// this property is used as the select query to execute that statement.
        /// </summary>
        String IdentitySelectStatement { get ;}
        /// <summary>
        /// Meta-data retrieval method that provides a list of parameters for a given stored procedure
        /// or function name. The name may contain a schema qualifier.
        /// </summary>
        /// <param name="procName">name of the procedure or function to look up in the database</param>
        /// <returns>a map of parameter names to DbParameterAccessor objects for procedure parameters</returns>
    	Dictionary<string, DbParameterAccessor> GetAllProcedureParameters(string procName);

        /// <summary>
        /// Meta-data retrieval method that provides a list of columns a given stored table
        /// or view. The name may contain a schema qualifier.
        /// </summary>
        /// <param name="procName">name of the table or view to look up in the database</param>
        /// <returns>a map of column names to DbParameterAccessor objects for table columns</returns>
    	
		Dictionary<string, DbParameterAccessor> GetAllColumns(string tableOrViewName);
        /// <summary>
        /// This method creates an insert command that will be used to populate new rows in a table
        /// </summary>
        /// <param name="tableName">table or view name to be used for inserting</param>
        /// <param name="accessors">list of columns that will be used in the insert command</param>
        /// <returns>Command string in the appropriate form, with parameter placeholders</returns>
        String BuildInsertCommand(String tableName, DbParameterAccessor[] accessors);
        /// <summary>
        ///  This method should create and initialise a DbCommand object based on the current
        /// IDbEnvironment connection and transaction. 
        /// </summary>
        /// <param name="statement">Command text to execute</param>
        /// <param name="commandType">Statement type</param>
        /// <returns>initialised DbCommand object</returns>
        DbCommand CreateCommand(string statement, CommandType commandType);
        /// <summary>
        /// Closes the current connection and rolls back any active transactions. The transactions
        /// are automatically rolled back to make tests repeatable.
        /// </summary>
        void CloseConnection();
        /// <summary>
        /// Connects to the database using a default database for the user
        /// </summary>
        /// <param name="dataSource">Host (optionally port), machine name or any other data source identifier</param>
        void Connect(String dataSource, String username, String password);
        
        /// <summary>
        /// Connects to the database using a specified database for the user
        /// </summary>
        /// <param name="dataSource">Host (optionally port), machine name or any other data source identifier</param>
        /// <param name="database">default database after connection</param>
        void Connect(String dataSource, String username, String password, String database);
        
        /// <summary>
        /// Connects using a database-specific connection string. This allows users to specify
        /// parameters that would not be used otherwise (i.e. windows integrated security)
        /// </summary>
        /// <param name="connectionString">full ADO.NET connection string</param>
    	void Connect(String connectionString);
        
        /// <summary>
        /// Load database properties from a file and connect.
        /// The connection properties file is a plain text file, containing key/value pairs separarted by the equals symbol (=). Lines starting with a hash (#) are ignored. Use the following keys (they care case-sensitive):
        /// 
        /// 1. service -- service name. In the previous example, it was LAPTOP\SQLEXPRESS.
        /// 2. username -- username to connect to the database. In the previous example, it was FitNesseUser.
        /// 3. password -- password to connect to the database. In the previous example, it was Password.
        /// 4. database -- optional fourth argument, allowing you to choose the active database. In the previous example, it was TestDB.
        /// 5. connection-string -- alternative to the four previous parameters, this allows you to specify the full connection string. This parameter should not be mixed with any of the four other keys. Use either the full string or specify individual properties. 
        /// 
        /// Here is an example:
        /// 
        /// # DBFit connection properties file
        /// #
        /// #1) Either specify full connection string
        /// #connection-string=
        /// #
        /// #2) OR specify service, username and password as separate properties
        /// service=localhost
        /// username=root
        /// password=
        /// #optionally specify a database name
        /// database=dbfit
        /// </summary>
        /// <param name="connectionPropertiesFile"></param>
        void ConnectUsingFile(String connectionPropertiesFile);

        /// <summary>
        /// bind any fixture symbols that match command parameters to this command. 
        /// symbols that have no matching parameters are ignored.
        /// </summary>
        /// <param name="dc"></param>
        void BindFixtureSymbols(DbCommand dc);

        /// <summary>
        /// Commit current transaction
        /// </summary>
        void Commit();

        /// <summary>
        /// Rollback current transaction
        /// </summary>
        void Rollback();
        /// <summary>
        /// Retrieve an exception code from a database exception. This method should perform
        /// any required conversion between a .NET exception and the real database error code.
        /// </summary>
        int GetExceptionCode(Exception ex);

        /// <summary>
        /// Utility method that creates an update command statement
        /// </summary>
        /// <param name="tableName">table or view being updated</param>
        /// <param name="updateAccessors">fields that are being modified</param>
        /// <param name="selectAccessors">fields used for selecting affected records</param>
        /// <returns></returns>
        String BuildUpdateCommand(String tableName, DbParameterAccessor[] updateAccessors, 
            DbParameterAccessor[] selectAccessors);
        /// <summary>
        /// Accessor for the current database connection, used by DBFit Fixtures
        /// </summary>
        DbConnection CurrentConnection { get; }
        /// <summary>
        /// Accessor for the current database transaction, used by DBFit Fixtures
        /// </summary>
        DbTransaction CurrentTransaction { get; }

    }
}