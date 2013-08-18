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
            DbObject dbObject = getTargetDbObject();
            if (rows == null) {//single execution, no args
                StatementExecution preparedStatement = dbObject.buildPreparedStatement(DbParameterAccessors.EMPTY);
                preparedStatement.run();
            } else {
                List<String> columnNames = FitHelpers.getCellTextFrom(rows.parts);
                DbParameterAccessors accessors = new HeaderRow(columnNames, dbObject).getAccessors();
                StatementExecution execution = dbObject.buildPreparedStatement(accessors.toArray());
                Parse row = rows;
                while ((row = row.more) != null) {
                    newRowTest(execution).runRow(new Row(accessors, row, this));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (rows == null) throw new Error(e);
            exception(rows.parts, e);
        }
    }

    protected RowAction newRowTest(StatementExecution execution) {
        return new RowAction(execution);
    }

}
