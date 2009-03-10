package org.dbfit.hsqldb;

import org.dbfit.core.AbstractDbEnvironment;

import javax.sql.DataSource;

/**
 * Provides support for testing HSQLDB databases.
 * Operates in Embedded mode.
 *
 * @author Jérôme Mirc, jerome.mirc@gmail.com
 */

public class EmbeddedHSQLDBEnvironment extends HSQLDBEnvironment {

    @Override
    protected String getConnectionString(String dataSource) {
        return String.format("jdbc:hsqldb:mem:%s", dataSource);
    }
}
