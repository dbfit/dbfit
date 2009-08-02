package dbfit.environment;

/**
 * Encapsulates support for the Derby database (also known as JavaDB). Operates
 * in Embedded mode.
 * 
 * @see DerbyEnvironment
 * @author P&aring;l Brattberg, pal.brattberg@acando.com
 */
public class EmbeddedDerbyEnvironment extends DerbyEnvironment {
	@Override
	protected String getConnectionString(String dataSource) {
		return String.format("jdbc:derby:%s;create=true", dataSource);
	}

	@Override
	protected String getConnectionString(String dataSource, String database) {
		return String.format("jdbc:derby:%s%s;create=true", dataSource, database);
	}

	@Override
	protected String getDriverClassName() {
		return "org.apache.derby.jdbc.EmbeddedDriver";
	}
}
