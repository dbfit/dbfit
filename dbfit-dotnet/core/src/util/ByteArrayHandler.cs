using System;
using System.Collections.Generic;
using System.Text;
using fitnesse.handlers;
using fit;
using System.Text.RegularExpressions;
namespace dbfit.util
{
	public class ByteArrayHandler: AbstractCellHandler
		{
			private static Regex matchExpression =
				new Regex("^0[Xx][A-Za-z0-9\\s]*");
			public override bool Match(string searchString, System.Type type)
			{
				return matchExpression.IsMatch(searchString) && 
					type.Equals(typeof(byte[]));
			}

			public override bool HandleEvaluate(Fixture fixture, Parse cell, Accessor accessor)
			{
				object actual=GetActual(accessor, fixture);
				if (actual== null) return false;

				byte[] actbyte = (byte[]) actual;
				byte[] expected=ParseArray(cell.Text);
				if (actbyte.Length!=expected.Length) return false;
				for (int i=0; i<actbyte.Length; i++)
					if (actbyte[i]!=expected[i]) return false;
				return true;
			}
			public override void HandleInput(Fixture fixture, Parse cell, Accessor accessor)
			{
				accessor.Set(fixture,ParseArray(cell.Text));
			}
			public static byte[] ParseArray(String txt){
				//return new byte[]{1,2,3};
				txt=txt.Replace(" ","");
				txt=txt.Substring(2);
				byte[] arr=new byte[txt.Length/2];
				for (int i=0; i<txt.Length; i+=2){
					String currentByte=txt.Substring(i,2);
					arr[i / 2] = Byte.Parse(currentByte, 
						System.Globalization.NumberStyles.HexNumber);
				}
				return arr;
			}
		}
	}
