package dbfit.util;

/**
 * Utility class for normalising identifiers.
 */
public class NameNormaliser {
	private NameNormaliser() {
		// utility classes should not be instanciated
	}

	private static String replaceIllegalCharactersWithSpacesRegex = "[^a-zA-Z0-9_.#]";

	private static String replaceIllegalCharacters(final String name) {
		return name.replaceAll(replaceIllegalCharactersWithSpacesRegex, "");
	}

	public static String normaliseName(final String name) {
		if (name == null)
			return "";
		return replaceIllegalCharacters(name.toLowerCase());
	}
}