package dbfit.fixture;

import dbfit.api.DbObject;
import dbfit.util.*;
import dbfit.util.actions.RowAction;
import dbfit.util.fit.FitHelpers;
import fit.Fixture;
import fit.Parse;

import java.sql.SQLException;
import java.util.List;

/**
 * this class handles all cases where a statement should be executed for each row with
 * given inputs and verifying optional outputs or exceptions. it also handles a special case
 * when just a single statement is executed without binding parameters to columns. Examples are
 * - Inserting data into tables/views
 * - Executing statements
 * - Updates
 * - Stored procedures/functions
 * <p/>
 * the object under test is defined by overriding getTargetObject. Unfortunately, because of the way FIT
 * instantiates fixtures, passing in an object using a constructor and aggregation simply doesn't do the trick
 * so users have to extend this fixture.
 */
public abstract class DbObjectExecutionFixture extends Fixture {
    /**
     * override this method and supply the dbObject implementation that will be executed for each row
     */
    protected abstract DbObject getTargetDbObject() throws SQLException;

    /**
     * executes the target dbObject for all rows of the table. if no rows are specified, executes
     * the target object only once
     */
    public void doRows(Parse rows) {
        try {
            new ExecutionTable(getTargetDbObject(), this, rows).run();
        } catch (Throwable e) {
            e.printStackTrace();
            if (rows == null) throw new Error(e);
            exception(rows.parts, e);
        }
    }

    protected RowAction newRowTest(StatementExecution execution) {
        return new RowAction(execution);
    }

    public static class ExecutionTable {
        private DbObject dbObject;
        private Fixture fixture;
        private Parse rows;
        private Parse currentRow;

        public ExecutionTable(DbObject dbObject, Fixture fixture, Parse rows) {
            this.dbObject = dbObject;
            this.fixture = fixture;
            this.rows = rows;
            if (rows != null)
                this.currentRow = rows.more;
        }

        public void run() throws Throwable {
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
}
