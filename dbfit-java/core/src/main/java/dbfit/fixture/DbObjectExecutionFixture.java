package dbfit.fixture;

import dbfit.api.DbObject;
import dbfit.util.*;
import fit.Binding;
import fit.Fixture;
import fit.Parse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static dbfit.util.Direction.*;

/**
 * this class handles all cases where a statement should be executed for each row with
 * given inputs and verifying optional outputs or exceptions. it also handles a special case
 * when just a single statement is executed without binding parameters to columns. Examples are
 * - Inserting data into tables/views
 * - Stored procedures/functions
 * <p/>
 * the object under test is defined by overriding getTargetObject. Unfortunately, because of the way FIT
 * instantiates fixtures, passing in an object using a constructor and aggregation simply doesn't do the trick
 * so users have to extend this fixture.
 */
public abstract class DbObjectExecutionFixture extends Fixture {
    private List<ParameterOrColumn> accessors = new ArrayList<ParameterOrColumn>();
    private Binding[] columnBindings;
    protected StatementExecution execution;
    protected DbObject dbObject; // intentionally private, subclasses should extend getTargetObject

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
            if (rows == null) {//single execution, no args
                StatementExecution preparedStatement = dbObject.buildPreparedStatement(accessors.toArray(new ParameterOrColumn[]{}));
                preparedStatement.run();
                return;
            }
            List<Heading> headings = getHeadingsFrom(rows.parts);

            accessors = getAccessors(headings);
            columnBindings = getColumnBindings(accessors, headings);
            StatementExecution preparedStatement = dbObject.buildPreparedStatement(accessors.toArray(new ParameterOrColumn[]{}));
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
     * initialise db parameters for the dbObject based on table header cells
     */
    private List<ParameterOrColumn> getAccessors(List<Heading> headings) throws SQLException {
        List<ParameterOrColumn> accessors = new ArrayList<ParameterOrColumn>();
        for (Heading heading : headings) {
            ParameterOrColumn parameterAccessor = dbObject.getDbParameterAccessor(heading.getName(),
                    heading.isOutput() ? OUTPUT : INPUT);
            if (parameterAccessor == null) {
                throw new IllegalArgumentException("Parameter/column " + heading.getName() + " not found");
            }
            accessors.add(parameterAccessor);
        }
        return accessors;
    }

    public static class Heading {
        private String name;

        public Heading(String name) {
            this.name = name;
        }

        public boolean isOutput() {
            return name.endsWith("?");
        }

        public String getName() {
            return name;
        }
    }

    /**
     * bind db accessors to columns based on the text in the header
     */
    private Binding[] getColumnBindings(List<ParameterOrColumn> accessors, List<Heading> headings) throws Exception {
        Binding[] columns = new Binding[headings.size()];
        for (Heading heading : headings) {
            int i = headings.indexOf(heading);
            if (heading.isOutput()) {
                columns[i] = new SymbolAccessQueryBinding();
            } else {
                columns[i] = new SymbolAccessSetBinding();
            }
            columns[i].adapter = new DbParameterAccessorTypeAdapter(accessors.get(i), this);
        }
        return columns;
    }

    private List<Heading> getHeadingsFrom(Parse headerCells) {
        List<Heading> headings = new ArrayList<Heading>();
        for (int i = 0; headerCells != null; i++, headerCells = headerCells.more) {
            String text = headerCells.text();
            headings.add(new Heading(text));
        }
        return headings;
    }

    /**
     * execute a single row
     */
    private void runRow(Parse row) throws Throwable {
        Parse cell = row.parts;
        //first set input params
        for (int column = 0; column < accessors.size(); column++, cell = cell.more) {
            if (accessors.get(column).hasDirection(INPUT)) {
                columnBindings[column].doCell(this, cell);
            }
        }
        executeStatementAndEvaluateOutputs(row);
    }

    protected void executeStatementAndEvaluateOutputs(Parse row)
            throws SQLException, Throwable {
        execution.run();
        Parse cells = row.parts;
        for (int column = 0; column < accessors.size(); column++, cells = cells.more) {
            if (accessors.get(column).hasDirection(OUTPUT) || accessors.get(column).hasDirection(RETURN_VALUE)) {
                columnBindings[column].doCell(this, cells);
            }
        }
    }
}
