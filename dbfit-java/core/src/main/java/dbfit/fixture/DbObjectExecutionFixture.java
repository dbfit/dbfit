package dbfit.fixture;

import dbfit.api.DbObject;
import dbfit.util.*;
import fit.Binding;
import fit.Fixture;
import fit.Parse;

import java.sql.SQLException;

import static dbfit.util.Direction.*;

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
    private DbParameterAccessor[] accessors = new DbParameterAccessor[0];
    private Binding[] columnBindings;
    private StatementExecution execution;
    private DbObject dbObject; // intentionally private, subclasses should extend getTargetObject

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
            dbObject = getTargetDbObject();
            if (dbObject == null) throw new Error("DB Object not specified!");
            if (rows == null) {//single execution, no args
                StatementExecution preparedStatement = dbObject.buildPreparedStatement(accessors);
                preparedStatement.run();
                return;
            }
            accessors = getAccessors(rows.parts);
            if (accessors == null) return;// error reading args
            columnBindings = getColumnBindings(rows.parts, accessors);
            StatementExecution preparedStatement = dbObject.buildPreparedStatement(accessors);
            execution = preparedStatement;
            Parse row = rows;
            while ((row = row.more) != null) {
                runRow(row);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (rows == null) throw new Error(e);
            exception(rows.parts, e);
        }
    }

    /**
     * does the column name map to an output argument
     */
    private static boolean isOutput(String name) {
        return name.endsWith("?");
    }

    private static DbParameterAccessor[] EMPTY = new DbParameterAccessor[0];

    /**
     * initialise db parameters for the dbObject based on table header cells
     */
    private DbParameterAccessor[] getAccessors(Parse headerCells) throws SQLException {
        if (headerCells == null) return EMPTY;
        DbParameterAccessor accessors[] = new DbParameterAccessor[headerCells.size()];
        for (int i = 0; headerCells != null; i++, headerCells = headerCells.more) {
            String name = headerCells.text();
            accessors[i] = dbObject.getDbParameterAccessor(name,
                    isOutput(name) ? OUTPUT : INPUT);
            if (accessors[i] == null) {
                exception(headerCells, new IllegalArgumentException("Parameter/column " + name + " not found"));
                return null;
            }
        }
        return accessors;
    }

    /**
     * bind db accessors to columns based on the text in the header
     */
    private Binding[] getColumnBindings(Parse headerCells, DbParameterAccessor[] accessors) throws Exception {
        if (headerCells == null) return new Binding[0];
        Binding[] columns = new Binding[headerCells.size()];
        for (int i = 0; headerCells != null; i++, headerCells = headerCells.more) {
            String name = headerCells.text();
            if (isOutput(name)) {
                columns[i] = new SymbolAccessQueryBinding();
            } else {
                columns[i] = new SymbolAccessSetBinding();
            }
            columns[i].adapter = new DbParameterAccessorTypeAdapter(accessors[i], this);
        }
        return columns;
    }

    /**
     * execute a single row
     */
    private void runRow(Parse row) throws Throwable {
        Parse cell = row.parts;
        //first set input params
        for (int column = 0; column < accessors.length; column++, cell = cell.more) {
            if (accessors[column].hasDirection(INPUT)) {
                columnBindings[column].doCell(this, cell);
            }
        }
        executeStatementAndEvaluateOutputs(row);
    }


    protected void executeStatementAndEvaluateOutputs(Parse row)
            throws SQLException, Throwable {
        execution.run();
        Parse cells = row.parts;
        for (int column = 0; column < accessors.length; column++, cells = cells.more) {
            if (accessors[column].hasDirection(OUTPUT) || accessors[column].hasDirection(RETURN_VALUE)) {
                columnBindings[column].doCell(this, cells);
            }
        }
    }

    protected StatementExecution getExecution() {
        return execution;
    }

    protected DbObject getDbObject() {
        return dbObject;
    }
}
