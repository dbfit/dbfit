using System;
using System.Collections.Generic;
using System.Text;

namespace dbfit.fixture
{
    public class SetParameter : fit.Fixture
    {
        public static void SetParameterValue(String name, Object value)
        {
            if (value == null || "null".Equals(value.ToString().ToLower()))
            {
                fit.Fixture.Save(name, DBNull.Value);
            }
            else if (value != null && value.ToString().StartsWith("<<"))
            {
                string varname = value.ToString().Substring(2);
                if (!name.Equals(varname))
                {
                    fit.Fixture.Save(name, fit.Fixture.Recall(varname));
                }
            }
            else
                fit.Fixture.Save(name, value);
        }
        public override void DoTable(fit.Parse table)
        {
            SetParameterValue(Args[0], GetArgumentInput(1, typeof(object)));
        }
    }
}
