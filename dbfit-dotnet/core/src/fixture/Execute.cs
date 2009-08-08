using System;
using System.Collections.Generic;
using System.Text;
using fit;
using System.Data;
using System.Data.Common;
namespace dbfit.fixture
{
    public class Execute:fit.Fixture
    {
        private IDbEnvironment environment;
        private String statement;
        public Execute()
        {
            environment = DbEnvironmentFactory.DefaultEnvironment;
        }
        public Execute(IDbEnvironment environment, String statement)
        {
            this.environment = environment;
            this.statement = statement;
        }
        public override void DoRows(Parse rows)
        {
            if (String.IsNullOrEmpty(statement))
                statement = Args[0];
            using (DbCommand dc = environment.CreateCommand(statement, CommandType.Text))
            {
                if (dbfit.util.Options.ShouldBindSymbols()) 
                    environment.BindFixtureSymbols(dc);
                dc.ExecuteNonQuery();
            }
        }
    }
}
