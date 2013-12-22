package dbfit.fixture;

import dbfit.api.DbObject;
import dbfit.util.DbParameterAccessors;
import dbfit.util.HeaderRow;
import dbfit.util.Row;
import dbfit.util.actions.RowAction;
import dbfit.util.fit.FitHelpers;
import fit.Fixture;
import fit.Parse;

import java.sql.SQLException;
import java.util.List;

public class ExecutionTable {
    private Fixture fixture;
    private Parse rows;
    private Parse currentRow;

    public ExecutionTable(Fixture fixture, Parse rows) {
        this.fixture = fixture;
        this.rows = rows;
        if (rows != null)
            this.currentRow = rows.more;
    }

    public void run() throws Throwable {
        DbObject dbObject = getTargetDbObject();
        if (!areDataRowsPresent()) {//single execution, no args
            StatementExecution preparedStatement = dbObject.buildPreparedStatement(DbParameterAccessors.EMPTY);
            preparedStatement.run();
        } else {
            DbParameterAccessors accessors = new HeaderRow(getColumnNames(), dbObject).getAccessors();
            StatementExecution execution = dbObject.buildPreparedStatement(accessors.toArray());
            RowAction action = newRowAction(execution);

            for (Row row = nextRow(accessors); row != null; row = nextRow(accessors)) {
                action.runRow(row);
            }
        }
    }

    protected DbObject getTargetDbObject() throws SQLException {
        throw new RuntimeException("should be implemented by subclasses");
    }

    protected boolean areDataRowsPresent() {
        return rows != null;
    }

    protected List<String> getColumnNames() {
        return FitHelpers.getCellTextFrom(rows.parts);
    }

    protected Row nextRow(DbParameterAccessors accessors) throws Throwable {
        if (currentRow == null) return null;
        Row row = new Row(accessors, currentRow, fixture);
        currentRow = currentRow.more;
        return row;
    }

    protected RowAction newRowAction(StatementExecution execution) {
        return new RowAction(execution);
    }
}
