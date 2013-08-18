package dbfit.util.actions;

import dbfit.util.Row;
import dbfit.fixture.StatementExecution;

import java.sql.SQLException;

public class SpecificExceptionRowAction extends RowAction {
    private Integer expectedErrorCode;

    public SpecificExceptionRowAction(StatementExecution execution,
                                      Integer expectedErrorCode) {
        super(execution);
        this.expectedErrorCode = expectedErrorCode;
    }

    @Override
    protected void evaluateOutputs(Row row) throws Throwable {
        if (execution.didExecutionSucceed()) {
            row.getTestResultHandler().fail("no exception raised");
        } else if (expectedErrorCode.equals(getActualErrorCodeFrom(execution))) {
            row.getTestResultHandler().pass();
        } else {
            row.getTestResultHandler().fail(" got error code " + getActualErrorCodeFrom(execution));
        }
    }

    private int getActualErrorCodeFrom(StatementExecution execution) {
        SQLException e = execution.getEncounteredException();
        return e.getErrorCode();
    }
}
