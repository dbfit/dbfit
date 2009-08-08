using System;
using System.Collections.Generic;
using System.Text;
using fitnesse.handlers;
using fit;
using System.Text.RegularExpressions;
namespace dbfit.util
{
	public class FixedLengthStringHandler: AbstractCellHandler
	{
			public override bool Match(string searchString, System.Type type)
			{
                return searchString.StartsWith("'") && searchString.EndsWith("'")
                &&type.Equals(typeof(System.String));
			}

			public override bool HandleEvaluate(Fixture fixture, Parse cell, Accessor accessor)
			{
            	object actual=GetActual(accessor, fixture);
				if (actual==null) return false;
                String cellText=cell.Text;
                String expected = cellText.Substring(1, cellText.Length - 2);
//                Console.WriteLine(
//                    ("[" + cellText + "][" + expected + "][" + actual + "]").Replace(" ","x")                    
//                    );
                return expected.Equals(actual);
			}
			public override void HandleInput(Fixture fixture, Parse cell, Accessor accessor)
			{
                String cellText=cell.Text;
                String actual= cellText.Substring(1, cellText.Length - 2);
				accessor.Set(fixture,actual);
			}
		}
	}
