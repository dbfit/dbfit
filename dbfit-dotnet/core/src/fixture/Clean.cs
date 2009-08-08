/// Copyright (C) Gojko Adzic 2006-2008 http://gojko.net
/// Released under GNU GPL 2.0
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Text;

using fit;


namespace dbfit.fixture {
    /// <summary>
    /// Deprecated fixture that can be used to clean up the database. Kept only for
    /// legacy compatibility. Use Execute fixture with a Delete command instead of this.
    /// </summary>
	public class Clean:fit.ColumnFixture {

        private IDbEnvironment environment;
		public Clean(IDbEnvironment environment) {
			this.environment = environment;
		}
        public Clean()
        {
            this.environment = DbEnvironmentFactory.DefaultEnvironment;
        }
        public String table;
        public String columnName;
        public Decimal[] ids;
        public String[] keys;
        public string where=null;
        private string getIDCSV()
        {
            StringBuilder sb = new StringBuilder();
            String comma = "";
            foreach (decimal x in ids)
            {
                sb.Append(comma);
                sb.Append(x.ToString());
                comma = ", ";
            }
            return sb.ToString();
        }
        private string getKeyCSV()
        {
            StringBuilder sb = new StringBuilder();
            String comma = "";
            foreach (String x in keys)
            {
                sb.Append(comma);
                sb.Append("'");
                sb.Append(x.ToString());
                sb.Append("'");
                comma = ", ";
            }
            return sb.ToString();
        }

        private bool hadRowOperation=false;		
		public bool clean() {
            DbCommand command = environment.CreateCommand(
                "Delete from " + table +(where!=null?" where "+where:""), CommandType.Text);
            command.ExecuteNonQuery();
			return true;
		}
        public bool DeleteRowsForIDs()
        {
            DbCommand command = environment.CreateCommand(
                "Delete from " + table + " where "+columnName +" in ("
                + getIDCSV()+") "+(where != null ? " and " + where : ""), CommandType.Text);
            command.ExecuteNonQuery();
        	hadRowOperation=true;
            return true;
        }
        public bool DeleteRowsForKeys()
        {
            DbCommand command = environment.CreateCommand(
                "Delete from " + table + " where " + columnName + " in ("
                + getKeyCSV() + ") " + (where != null ? " and " + where : ""), CommandType.Text);
            command.ExecuteNonQuery();
        	hadRowOperation=true;
        	return true;
        }
		public override void DoRow(Parse row) {
			hadRowOperation = false;
			base.DoRow(row);
			if (!hadRowOperation) {
				clean();
			}
		}
	}
}