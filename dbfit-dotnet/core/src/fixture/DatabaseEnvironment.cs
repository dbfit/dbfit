/// Copyright (C) Gojko Adzic 2006-2008 http://gojko.net
/// Released under GNU GPL 2.0
using System;
using System.Collections.Generic;
using System.Text;

namespace dbfit.fixture
{
    /// <summary>
    /// Utility fixture that provides database control features in standalone mode. This fixture
    /// should be used to connect/disconnect and manipulate transactions when the flow mode
    /// cannot be used. It is also used to control dbfit options and advanced features. 
    /// 
    /// If it has a fixture argument, that argument is used to initialise a new IDbEnvironment object. 
    /// The argument value should be the type of required IDbInstance implementation, like 
    /// ORACLE, SQLSERVER, SQLSERVER2000 or DB2. That object is then stored for later use in the 
    /// DbEnvironmentFactory singleton.
    /// 
    /// If there are no fixture arguments, then the last previously initialised
    /// IDbEnvironment object is used.
    /// 
    /// </summary>
    public class DatabaseEnvironment : fitlibrary.SequenceFixture
    {
		 public DatabaseEnvironment(){
			this.mySystemUnderTest=DbEnvironmentFactory.DefaultEnvironment;
	     }
		 public override void DoTable(fit.Parse theTable)
		 {
			if (Args.Length>0){
				IDbEnvironment env;
				String requestedEnv=Args[0].ToUpper().Trim();
/*                if ("ORACLE".Equals(requestedEnv))
                    env = new OracleEnvironment();
                else if ("SQLSERVER".Equals(requestedEnv))
                    env = new SqlServerEnvironment();
                else if ("SQLSERVER2000".Equals(requestedEnv))
                    env = new SqlServer2000Environment();
                else if ("DB2".Equals(requestedEnv))
                    env = new DB2Environment();
 */ 
                throw new ApplicationException("DB Environment not supported " + requestedEnv);
				DbEnvironmentFactory.DefaultEnvironment=env;
				this.mySystemUnderTest=env;
			}
			base.DoTable(theTable);
		 }        
		 
        /// <summary>
        /// set the value of DbFit options. See dbfit.util.options for more information
        /// </summary>
        /// <param name="option">option name</param>
        /// <param name="value">option vaue</param>
        public void SetOption(String option, String value)
        {
            dbfit.util.Options.SetOption(option, value);
        }
    }
}
