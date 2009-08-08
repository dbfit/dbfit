using System;
using System.Collections.Generic;
using System.Text;

namespace dbfit.util
{
	public class Export:fit.Fixture
	{
		public override void DoCell(fit.Parse cell, int columnNumber)
		{
            fit.Configuration.Instance.Namespaces.Remove(cell.Text);
		}
	}
}
