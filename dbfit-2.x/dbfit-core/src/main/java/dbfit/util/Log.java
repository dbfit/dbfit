package dbfit.util;

/**
 * Provides basic support for logging and debugging for dbfit.
 * 
 * @author Pål Brattberg, Acando AB
 * @since 1.1.2
 */
public class Log {

	public static void log(String msg) {
		write(msg);
	}

	public static void log(String msg, Object... args) {
		writef(msg + "\n", args);
	}

	private static void write(String msg) {
		writef("%s%s", msg, "\n");
	}

	public static void log() {
		write("");
	}

	private static void writef(String msg, Object... args) {
		if (Options.isDebugLog()) {
			System.out.printf(msg, args);
		}
	}

	public static void log(final Exception e) {
		if (Options.isDebugLog()) {
			e.printStackTrace();
		}
	}
}
