package dbfit.util.actions;

import dbfit.util.Row;
import dbfit.fixture.StatementExecution;
import dbfit.util.Cell;

public class RowAction {
    protected StatementExecution execution;

    public RowAction(StatementExecution execution) {
        this.execution = execution;
    }

    /**
     * execute a single row
     */
    public void runRow(Row row) throws Throwable {
        setInputs(row);
        run();
        evaluateOutputs(row);
    }

    private void run() {
        execution.run();
    }

    protected void setInputs(Row row) throws Throwable {
        for (Cell cell : row.getInputCells()) {
            doCell(cell);
        }
    }

    protected void evaluateOutputs(Row row) throws Throwable {
        for (Cell cell : row.getOutputCells()) {
            doCell(cell);
        }
    }

    private void doCell(Cell cell) throws Throwable {
        try {
            new MostAppropriateAction().run(cell);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
