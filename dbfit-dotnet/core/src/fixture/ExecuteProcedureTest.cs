using System;
using System.Collections.Generic;
using System.Text;

using MbUnit.Framework;

namespace dbfit.fixture
{
    [TestFixture]
    public class ExecuteProcedureTest
    {
        [Test]
        public void SortAccessorsInRightOrder()
        {
            //Prepare
            DbParameterAccessor[] accessorsToOrder = new DbParameterAccessor[4];
            accessorsToOrder[0]=new DbParameterAccessor(new System.Data.SqlClient.SqlParameter(), typeof(string), 1, "String");
            accessorsToOrder[3]=new DbParameterAccessor(new System.Data.SqlClient.SqlParameter(), typeof(string), 3, "String");
            accessorsToOrder[2]=new DbParameterAccessor(new System.Data.SqlClient.SqlParameter(), typeof(string), 5, "String");
            accessorsToOrder[1]=new DbParameterAccessor(new System.Data.SqlClient.SqlParameter(), typeof(string), 7, "String");

            
            //Execute
            DbParameterAccessor[] resultingAccessors = ExecuteProcedure.SortAccessors(accessorsToOrder);

            //Verify
            Assert.AreEqual(1, resultingAccessors[0].Position);
            Assert.AreEqual(3, resultingAccessors[1].Position);
            Assert.AreEqual(5, resultingAccessors[2].Position);
            Assert.AreEqual(7, resultingAccessors[3].Position);
        }

    }

}
