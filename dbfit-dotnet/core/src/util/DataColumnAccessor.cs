using System;
using System.Data;

using fit;

namespace dbfit {
	public class ColumnMissingException : ApplicationException {
		public ColumnMissingException(String name) : base("Cannot find column " + name) { }
	}
	public class DataColumnAccessor : AbstractAccessor {
		private DataColumn column;
		private bool usedForMatching;
		public DataColumnAccessor(DataColumn c, bool usedForMatching)
			: base(c.DataType) {
			column = c;
			this.usedForMatching = usedForMatching;
		}
		public override object Get(Fixture fixture) {
			DataRow dr = (DataRow)fixture.GetTargetObject();
			return GetValue(dr, column);
		}
		public override void Set(Fixture fixture, object value) {
			throw new NotSupportedException("data columns are read only");
		}
		public static object GetValue(DataRow dr, DataColumn column) {			
            object value = dr[column];
            //Console.WriteLine(column.ColumnName + ":" + value.GetType());
			return (DBNull.Value.Equals(value) ? null : value);
		}
		public bool IsUsedForMatching() {
			return usedForMatching;
		}
	}
}
