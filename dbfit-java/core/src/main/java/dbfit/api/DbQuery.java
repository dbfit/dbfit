package dbfit.api;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An object representing an executable database operation.
 * Executing the operation returns a ResultSet result.
 *
 */
public interface DbQuery extends AutoCloseable {

    /**
     * Executes the database command and return a ResultSet
     * object for its result.
     *
     * @return a ResultSet object that contains the data produced by the
     *         command execution.
     *
     * @throws SQLException if a database error occurs.
     */
    ResultSet executeQuery() throws SQLException;

    /**
     * Releases underlying database and JDBC resources.
     *
     * Calling method close on an object which is already
     * closed has no effect.
     */
    @Override
    void close() throws SQLException;
}
