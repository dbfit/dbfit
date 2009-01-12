package dbfit.util;

public class NoMatchingRowFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoMatchingRowFoundException() {
		super("No matching row found");
	}
}
