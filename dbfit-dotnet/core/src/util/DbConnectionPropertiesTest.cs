using System;
using System.Collections.Generic;
using System.Text;

using MbUnit.Framework;

namespace dbfit.util
{
    [TestFixture]
	
    public class DbConnectionPropertiesTest    
    {
        [Test]
        public void TestWithConnectionString()
        {
            DbConnectionProperties props=DbConnectionProperties.CreateFromString(
                @"connection-string=test1234");
            Assert.AreEqual("test1234", props.FullConnectionString);
            Assert.IsNull(props.Username);
            Assert.IsNull(props.Password);
            Assert.IsNull(props.Service);
            Assert.IsNull(props.DbName);
        }
        [Test]
        public void TestWithConnectionStringWithEquals()
        {
            DbConnectionProperties props = DbConnectionProperties.CreateFromString(
                @"connection-string=test1234&Username=US&Password=PW");
            Assert.AreEqual("test1234&Username=US&Password=PW", props.FullConnectionString);
            Assert.IsNull(props.Username);
            Assert.IsNull(props.Password);
            Assert.IsNull(props.Service);
            Assert.IsNull(props.DbName);
        }
        [Test]
        public void TestWithSplitProperties()
        {
            DbConnectionProperties props = DbConnectionProperties.CreateFromString(
                @"service=testsvc
                  username=testuser
                  password=testpwd
                  database=testdb");
            Assert.IsNull(props.FullConnectionString);
            Assert.AreEqual("testuser",props.Username);
            Assert.AreEqual("testpwd",props.Password);
            Assert.AreEqual("testsvc",props.Service);
            Assert.AreEqual("testdb",props.DbName);
        }
        [Test]
        public void TestCommentsAndEmptyLines()
        {
            DbConnectionProperties props = DbConnectionProperties.CreateFromString(
                @"service=testsvc

                  username=testuser
                  password=testpwd
                  #this is a comment
                  database=testdb
                ");
            Assert.IsNull(props.FullConnectionString);
            Assert.AreEqual("testuser", props.Username);
            Assert.AreEqual("testpwd", props.Password);
            Assert.AreEqual("testsvc", props.Service);
            Assert.AreEqual("testdb", props.DbName);
        }

    }
}
