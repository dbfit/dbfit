using System;
using System.Collections.Generic;
using System.Text;
using System.Data.Common;
using System.Data;
namespace dbfit.fixture
{
    public class Query : AbstractDataTableFixture
    {
        private IDbEnvironment environment;
        private DataTable dataTable;
        private bool isOrdered;

        public Query(){        
            this.environment=DbEnvironmentFactory.DefaultEnvironment;
            this.isOrdered = false;
        }
        public Query(IDbEnvironment environment, String query, bool isOrdered)
        {
            this.environment = environment;
            this.isOrdered = isOrdered;
            this.dataTable = GetDataTable(query);
        }
        protected override DataTable GetDataTable()
        {
            if (this.dataTable == null)
                this.dataTable = GetDataTable(Args[0]);
            return dataTable;
        }
        protected override bool IsOrdered { get { return isOrdered; } }

        private DataTable GetDataTable(String query)
        {
            if (query.StartsWith("<<"))
            {
                string varname = query.ToString().Substring(2);
                return (DataTable)fit.Fixture.Recall(varname);
            }
            else
            {
                return GetDataTable(query,environment);
            }
        }

        public static DataTable GetDataTable(String query,IDbEnvironment environment)
        {

            DbCommand dc = environment.CreateCommand(query, CommandType.Text);
            if (dbfit.util.Options.ShouldBindSymbols())
                environment.BindFixtureSymbols(dc);

            DbDataAdapter oap = environment.DbProviderFactory.CreateDataAdapter();
            oap.SelectCommand = dc;
            DataSet ds = new DataSet();
            oap.Fill(ds);
            dc.Dispose();
            return ds.Tables[0];
        }
    }
}
