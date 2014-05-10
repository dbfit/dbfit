package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;

/**
 * Encapsulates support for the Derby database (also known as JavaDB). Operates
 * in Embedded mode.
 * 
 * @see DerbyEnvironment
 * @author P&aring;l Brattberg, pal.brattberg@acando.com
 */
@DatabaseEnvironment(name="EmbeddedDerby", driver="org.apache.derby.jdbc.EmbeddedDriver")
public class EmbeddedDerbyEnvironment extends DerbyEnvironment {
    public EmbeddedDerbyEnvironment(String driverClassName) {
        super(driverClassName);
    }

    @Override
    protected String getConnectionString(String dataSource) {
        return String.format("jdbc:derby:%s;create=true", dataSource);
    }

    @Override
    protected String getConnectionString(String dataSource, String database) {
        return String.format("jdbc:derby:%s%s;create=true", dataSource,
                database);
    }
}

