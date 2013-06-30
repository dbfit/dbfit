package dbfit.fixture;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;

public class StatementExecution {
    private Savepoint savepoint;
    private PreparedStatement statement;

    public StatementExecution(PreparedStatement statement) {
        this.statement = statement;
        try {
            statement.clearParameters();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createSavepoint() {
        String savepointName = "eee" + this.hashCode();
        if (savepointName.length() > 10) savepointName = savepointName.substring(1, 9);
        savepoint = null;

        try {
            savepoint = statement.getConnection().setSavepoint(savepointName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void restoreSavepoint() {
        if (savepoint != null) {
            try {
                statement.getConnection().rollback(savepoint);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() throws SQLException {
        statement.execute();
    }
}
