using System;
using System.Collections.Generic;
using System.Text;
using dbfit;
using dbfit.fixture;
// THIS IS A LEGACY COMPATIBILITY NAMESPACE, FOR EASY MIGRATION FROM
// PRE 0.9 TESTS TO NEW TESTS. MIGRATION TO NEW NAMES IS STRONGLY SUGGESTED
namespace dbfit.oldnames
{
    public class DataTableFixture : Query
    {
        public DataTableFixture() { }
        public DataTableFixture(IDbEnvironment environment, String query, bool isOrdered)
                :base(environment, query, isOrdered){}
    }
    public class StatementFixture : Execute
    {
        public StatementFixture() {}
        public StatementFixture(IDbEnvironment environment, 
                String statement):base(environment,statement){}
    }
    public class StoredProcedureFixture : ExecuteProcedure
    {
        public StoredProcedureFixture() { }
        public StoredProcedureFixture(IDbEnvironment environment,
                String statement)
            : base(environment, statement) { }
    }

    public class InsertFixture : Insert
    {
        public InsertFixture() { }
        public InsertFixture(IDbEnvironment environment,
                String statement)
            : base(environment, statement) { }
    }

}
