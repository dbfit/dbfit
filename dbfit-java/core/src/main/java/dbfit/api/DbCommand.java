package dbfit.api;

import java.sql.SQLException;

/**
 * An object representing an executable database operation
 * with no result returned.
 */
public interface DbCommand extends AutoCloseable {

    /**
     * Executes the database command.
     *
     * @throws SQLException if a database error occurs.
     */
    void execute() throws SQLException;

    /**
     * Releases underlying database and JDBC resources.
     *
     * Calling method close on an object which is already closed has no effect.
     */
    @Override
    void close() throws SQLException;
}
