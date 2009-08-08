using System;
using System.Collections.Generic;
using System.Text;
using System.Data.Common;
using System.Data;
namespace dbfit.util
{
    public class IdRetrievalAccessor: fit.AbstractAccessor
    {
        private IDbEnvironment environment;
        private Type expectedType;
        public IdRetrievalAccessor(IDbEnvironment environment, Type expectedType):base(expectedType)
        {
            this.environment = environment;
            this.expectedType = expectedType;
        }
        public override object Get(fit.Fixture fixture)
        {
            if (environment.SupportsReturnOnInsert)
                throw new ApplicationException(environment.GetType() + 
                    " supports return on insert, IdRetrievalAccessor should not be used");
            DbCommand cmd = environment.CreateCommand(environment.IdentitySelectStatement, CommandType.Text);
         //   Console.WriteLine(environment.IdentitySelectExpression);
            object value = cmd.ExecuteScalar();
            value=Convert.ChangeType(value, expectedType);
            //Console.WriteLine("value=" + value + " of " + value.GetType());
            return (DBNull.Value.Equals(value) ? null : value);
        }
        public override void Set(fit.Fixture fixture, object value)
        {
        }
    }
}
