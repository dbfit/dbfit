package dbfit.util;

import java.sql.Statement;
import java.sql.SQLException;

public class DdlStatementExecution implements AutoCloseable {
    private Statement statement;
    private String commandText;

    public DdlStatementExecution(Statement statement, String commandText) {
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
