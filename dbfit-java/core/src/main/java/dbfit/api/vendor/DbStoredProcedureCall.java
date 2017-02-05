package dbfit.api.vendor;

import java.sql.SQLException;

import dbfit.fixture.StatementExecution;

public interface DbStoredProcedureCall {

    StatementExecution toStatementExecution() throws SQLException;
}
