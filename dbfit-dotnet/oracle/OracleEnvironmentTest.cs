using System;
using System.Collections.Generic;
using System.Text;

using MbUnit.Framework;

namespace dbfit {
	[TestFixture]
	public class OracleEnvironmentTest {
		private OracleEnvironment oe=new OracleEnvironment();
		[Test]
		public void CheckEmptyParams() {
			Assert.AreEqual(0,oe.ExtractParamNames("select * from dual").Length);			
		}
		[Test]
		public void CheckSingleParam() {
			Assert.AreEqual(new string[]{"mydate"}, oe.ExtractParamNames("select * from dual where sysdate<:mydate"));
		}
		[Test]
		public void CheckMultipleParams() {
			string[] paramnames=oe.ExtractParamNames("select :myname as zeka from dual where sysdate<:mydate");
			Assert.AreEqual(2,paramnames.Length);
            Assert.Contains(paramnames, "mydate");
            Assert.Contains(paramnames, "myname");
		}
		[Test]
		public void CheckMultipleParamsRecurring() {
			string[] paramnames = oe.ExtractParamNames("select :myname,length(:myname) as l, :myname || :mydate as zeka2 from dual where sysdate<:mydate");
			Assert.AreEqual(2, paramnames.Length);
			Assert.Contains(paramnames, "mydate");
			Assert.Contains(paramnames,"myname");
		}
		[Test]
		public void CheckUnderscore() {
			Assert.AreEqual(new string[] { "my_date" }, oe.ExtractParamNames("select * from dual where sysdate<:my_date"));
		}
	}
}
