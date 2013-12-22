package dbfit.util.actions;

import dbfit.util.Row;
import dbfit.fixture.StatementExecution;

public class AnyExceptionRowAction extends RowAction {
    public AnyExceptionRowAction(StatementExecution execution) {
        super(execution);
    }

    @Override
    protected void evaluateOutputs(Row row) throws Throwable {
        if (execution.didExecutionSucceed()) {
            row.getTestResultHandler().fail("no exception raised");
        } else {
            row.getTestResultHandler().pass();
        }
    }
}
