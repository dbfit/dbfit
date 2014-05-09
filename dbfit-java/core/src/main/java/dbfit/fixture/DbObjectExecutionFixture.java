package dbfit.fixture;

import dbfit.api.DbObject;
import dbfit.util.*;
import fit.Binding;
import fit.Fixture;
import fit.Parse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private DbParameterAccessors accessors = new DbParameterAccessors();
    private Map<DbParameterAccessor, Binding> columnBindings;
    private StatementExecution execution;
    private DbObject dbObject; // intentionally private, subclasses should extend getTargetObject

    /**
     * override this method to control whether an exception is expected or not. By default, expects no exception to happen
     */
    protected ExpectedBehaviour getExpectedBehaviour() {
        return ExpectedBehaviour.NO_EXCEPTION;
    }

    /**
     * override this method and supply the expected exception number, if one is expected
     */
    protected int getExpectedErrorCode() {
        return 0;
    }

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
                try (StatementExecution preparedStatement =
                        dbObject.buildPreparedStatement(accessors.toArray())) {
                    preparedStatement.run();
                    return;
                }
            }
            List<String> columnNames = getColumnNamesFrom(rows.parts);
            accessors = getAccessors(rows.parts, columnNames);
            if (accessors == null) return;// error reading args
            columnBindings = getColumnBindings();
            try (StatementExecution preparedStatement
                    = dbObject.buildPreparedStatement(accessors.toArray())) {
                execution = preparedStatement;
                Parse row = rows;
                while ((row = row.more) != null) {
                    runRow(row);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (rows == null) {
                throw new Error(e);
            }
            exception(rows.parts, e);
        }
    }

    /**
     * does the column name map to an output argument
     */
    private static boolean isOutput(String name) {
        return name.endsWith("?");
    }

    private List<String> getColumnNamesFrom(Parse headerCells) {
        List<String> columnNames = new ArrayList<String>();
        for (; headerCells != null; headerCells = headerCells.more) {
            columnNames.add(headerCells.text());
        }
        return columnNames;
    }

    private DbParameterAccessors getAccessors(Parse headerRow, List<String> columnNames) throws SQLException {
        try {
            return getAccessors(columnNames);
        } catch (IllegalArgumentException e) {
            exception(headerRow, e);
            return null;
        }
    }

    /**
     * initialise db parameters for the dbObject based on table header cells
     */
    private DbParameterAccessors getAccessors(List<String> columnNames) throws SQLException {
        DbParameterAccessors accessors = new DbParameterAccessors();
        for (String name : columnNames) {
            DbParameterAccessor accessor = dbObject.getDbParameterAccessor(name, isOutput(name) ? OUTPUT : INPUT);
            if (accessor == null) {
                throw new IllegalArgumentException("Parameter/column " + name + " not found");
            }
            accessors.add(accessor);
        }
        return accessors;
    }

    /**
     * bind db accessors to columns based on the text in the header
     */
    private Map<DbParameterAccessor, Binding> getColumnBindings() throws Exception {
        Map<DbParameterAccessor, Binding> bindings = new HashMap<DbParameterAccessor, Binding>();
        for (DbParameterAccessor accessor : accessors.toArray()) {
            Binding binding = (accessor.hasDirection(INPUT) ? new SymbolAccessSetBinding() : new SymbolAccessQueryBinding());
            binding.adapter = new DbParameterAccessorTypeAdapter(accessor, this);
            bindings.put(accessor, binding);
        }
        return bindings;
    }

    /**
     * execute a single row
     */
    private void runRow(Parse row) throws Throwable {
        //first set input params
        Map<DbParameterAccessor, Parse> cellMap = accessors.zipWith(asCellList(row));
        for (DbParameterAccessor inputAccessor : accessors.getInputAccessors()) {
            Parse cell = cellMap.get(inputAccessor);
            columnBindings.get(inputAccessor).doCell(this, cell);
        }

        if (getExpectedBehaviour() == ExpectedBehaviour.NO_EXCEPTION) {
            executeStatementAndEvaluateOutputs(row);
        } else {
            executeStatementExpectingException(row);
        }
    }

    private void executeStatementExpectingException(Parse row) throws Exception {
        try {
            execution.run();
            wrong(row);
        } catch (SQLException e) {
            e.printStackTrace();
            // all good, exception expected
            if (getExpectedBehaviour() == ExpectedBehaviour.ANY_EXCEPTION) {
                right(row);
            } else {
                int realError = e.getErrorCode();
                if (realError == getExpectedErrorCode())
                    right(row);
                else {
                    wrong(row);
                    row.parts.addToBody(fit.Fixture.gray(" got error code " + realError));
                }
            }
        }
    }

    private void executeStatementAndEvaluateOutputs(Parse row)
            throws SQLException, Throwable {
        execution.run();
        Map<DbParameterAccessor, Parse> cellMap = accessors.zipWith(asCellList(row));
        for (DbParameterAccessor outputAccessor : accessors.getOutputAccessors()) {
            Parse cell = cellMap.get(outputAccessor);
            columnBindings.get(outputAccessor).doCell(this, cell);
        }
    }

    private List<Parse> asCellList(Parse row) {
        List<Parse> cells = new ArrayList<Parse>();
        for (Parse cell = row.parts; cell != null; cell = cell.more) {
            cells.add(cell);
        }
        return cells;
    }
}
