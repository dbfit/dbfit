using System;
using System.Collections.Generic;
using System.Text;
using System.Data;
using System.Data.Common;
namespace dbfit.fixture
{
    public class QueryStats:fit.ColumnFixture
    {
        private IDbEnvironment environment;
        public QueryStats()
        {
            environment = DbEnvironmentFactory.DefaultEnvironment;
        }
        public QueryStats(IDbEnvironment environment)
        {
            this.environment = environment;
        }
        public string TableName;
        public string ViewName { set { TableName=value;}}
        public string Where;
        public string Query;
        private bool hasExecuted=false;
        public override void Reset()
        {
            hasExecuted = false;
            Where = null;
            Query = null;
            _rows = 0;
            TableName = null;
        }
        private int _rows;
        private void execQuery()
        {
            if (hasExecuted) return;
            if (Query == null)
            {
                Query = "select count(*) from " + TableName + (Where != null ? " where " + Where : "");
                DbCommand dc = environment.CreateCommand(Query, CommandType.Text);
                object o=dc.ExecuteScalar();
                dc.Dispose();
                if (o != null) _rows = Convert.ToInt32(o);
            }
            else
            {
                DbCommand dc = environment.CreateCommand(Query, CommandType.Text);
                environment.BindFixtureSymbols(dc);

                DbDataAdapter oap = environment.DbProviderFactory.CreateDataAdapter();
                oap.SelectCommand = dc;
                DataSet ds = new DataSet();
                oap.Fill(ds);
                dc.Dispose();
                _rows=ds.Tables[0].Rows.Count;
            }
            hasExecuted = true;
        }
        public int RowCount()
        {
            execQuery();
            return _rows; 
        }
        public bool IsEmpty()
        {
            return RowCount()==0; 
        }
    }
}
