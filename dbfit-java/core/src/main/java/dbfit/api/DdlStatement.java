package dbfit.api;

import java.sql.Statement;
import java.sql.SQLException;

public class DdlStatement implements AutoCloseable {
    private Statement statement;
    private String commandText;

    public DdlStatement(Statement statement, String commandText) {
        this.statement = statement;
        this.commandText = commandText;
    }

    public void run() throws SQLException {
        statement.execute(commandText);
    }

    @Override
    public void close() throws SQLException {
        statement.close();
    }
}
