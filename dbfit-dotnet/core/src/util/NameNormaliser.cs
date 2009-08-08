using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;

namespace dbfit.util {
	public class NameNormaliser {
		private static Regex replaceIllegalCharactersWithSpacesRegex = new Regex(@"[^a-zA-Z0-9_.#]");
		private static string ReplaceIllegalCharacters(string name) {
			return replaceIllegalCharactersWithSpacesRegex.Replace(name, "");
		}
		public static String NormaliseName(String name) {
			if (name == null) return "";
			return ReplaceIllegalCharacters(name.ToLower());
		}
	}
}
