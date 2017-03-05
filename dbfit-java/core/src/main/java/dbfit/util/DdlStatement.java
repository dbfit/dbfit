package dbfit.util;

import dbfit.api.DbCommand;

import java.sql.Statement;
import java.sql.SQLException;

public class DdlStatement implements DbCommand {
    private Statement statement;
    private String commandText;

    public DdlStatement(Statement statement, String commandText) {
        this.statement = statement;
        this.commandText = commandText;
    }

    @Override
    public void execute() throws SQLException {
        statement.execute(commandText);
    }

    @Override
    public void close() throws SQLException {
        statement.close();
    }
}
