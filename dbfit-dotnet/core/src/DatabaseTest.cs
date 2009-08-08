using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Text.RegularExpressions;
using dbfit.fixture;
using fit;

using fitlibrary;

namespace dbfit
{

    public class DatabaseTest : SequenceFixture
    {
        protected IDbEnvironment environment;
        //Dictionary<string, object> parameters = new Dictionary<string, object>();

        public override void DoTables(Parse theTables)
        {
            dbfit.util.Options.reset();
            base.DoTables(theTables);
            environment.CloseConnection();
        }
        public DatabaseTest(IDbEnvironment environment)
        {
            this.environment = environment;
        }
        public void Connect(String dataSource, String username, String password, String database)
        {
            this.environment.Connect(dataSource, username, password, database);
        }
        public void Connect(String dataSource, String username, String password)
        {
            this.environment.Connect(dataSource, username, password);
        }
        public void Connect(String connectionString)
        {
            this.environment.Connect(connectionString);
        }
        public void ConnectUsingFile(String path)
        {
            this.environment.ConnectUsingFile(path);
        }
        public void Close()
        {
            this.environment.CloseConnection();
        }

        public void SetParameter(String name, object value)
        {
            dbfit.fixture.SetParameter.SetParameterValue(name, value);
        }
        public void ClearParameters()
        {
            fit.Fixture.ClearSaved();
        }
        public Fixture Query(String query)
        {
            return new Query(environment, query, false);
        }
        public Fixture Update(String table)
        {
            return new Update(environment, table);
        }
        public Fixture OrderedQuery(String query)
        {
            return new Query(environment, query, true);
        }
        public Fixture Execute(String statement)
        {
            return new Execute(environment, statement);
        }
        public Fixture Insert(String table)
        {
            return new Insert(environment, table);
        }
        public Fixture ExecuteProcedure(String procedure)
        {
            return new ExecuteProcedure(environment, procedure);
        }
        public Fixture ExecuteProcedureExpectException(String procedure)
        {
            return new ExecuteProcedure(environment, procedure, true);
        }
        public Fixture ExecuteProcedureExpectException(String procedure, int errorCode)
        {
            return new ExecuteProcedure(environment, procedure, errorCode);
        }
        public void Commit()
        {
            environment.Commit();
        }
        public void Rollback()
        {
            environment.Rollback();
        }
        public Fixture QueryStats()
        {
            return new QueryStats(environment);
        }
        public Fixture Clean()
        {
            return new Clean(environment);
        }
        public Fixture InspectProcedure(String procedure)
        {
            return new Inspect(environment, Inspect.MODE_PROCEDURE, procedure);
        }
        public Fixture InspectTable(String table)
        {
            return new Inspect(environment, Inspect.MODE_TABLE, table);
        }
        public Fixture InspectView(String view)
        {
            return new Inspect(environment, Inspect.MODE_TABLE, view);
        }
        public Fixture InspectQuery(String query)
        {
            return new Inspect(environment, Inspect.MODE_QUERY, query);
        }
        public Fixture StoreQuery(String query, String symbolName)
        {
            return new StoreQuery(environment, query, symbolName);
        }
        public Fixture CompareStoredQueries(String symbol1, String symbol2)
        {
            return new CompareStoredQueries(environment, symbol1, symbol2);
        }
        public void SetOption(String option, String value)
        {
            dbfit.util.Options.SetOption(option, value);
        }
    }
}
