package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessorTypeAdapter;
import dbfit.util.NameNormaliser;
import dbfit.util.SymbolAccessSetBinding;
import fit.Binding;
import fit.Parse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dbfit.util.Direction;

public class Update extends fit.Fixture {
    private DBEnvironment environment;
    private StatementExecution statement;
    private String tableName;
    private Binding[] columnBindings;
    private DbParameterAccessor[] updateAccessors;
    private DbParameterAccessor[] selectAccessors;

    public Update() {
        this.environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public Update(DBEnvironment dbEnvironment) {
        this.environment = dbEnvironment;
    }

    public Update(DBEnvironment dbEnvironment, String tableName) {
        this.tableName = tableName;
        this.environment = dbEnvironment;
    }

    private StatementExecution buildUpdateCommand() throws SQLException {
        if (updateAccessors.length == 0) {
            throw new Error("Update fixture must have at least one field to update. Have you forgotten = after the column name?");
        }

        StringBuilder s = new StringBuilder("update ").append(tableName).append(" set ");

        for (int i = 0; i < updateAccessors.length; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(updateAccessors[i].getName()).append("=").append("?");
        }

        s.append(" where ");

        for (int i = 0; i < selectAccessors.length; i++) {
            if (i > 0) {
                s.append(" and ");
            }
            s.append(selectAccessors[i].getName()).append("=").append("?");
        }

        StatementExecution cs =
            new StatementExecution(environment.getConnection().prepareStatement(s.toString()));

        for (int i = 0; i < updateAccessors.length; i++) {
            updateAccessors[i].bindTo(cs, i + 1);
        }

        for (int j = 0; j < selectAccessors.length; j++) {
            selectAccessors[j].bindTo(cs, j + updateAccessors.length + 1);
        }
        return cs;
    }

    public void doRows(Parse rows) {
        // if table not defined as parameter, read from fixture argument; if still not defined, read from first row
        if ((tableName == null || tableName.trim().length() == 0) && args.length > 0) {
            tableName = args[0];
        } else if (tableName == null) {
            tableName = rows.parts.text();
            rows = rows.more;
        }

        try {
            initParameters(rows.parts); //init parameters from the first row
            try (StatementExecution st = buildUpdateCommand()) {
                statement = st;
                Parse row = rows;
                while ((row = row.more) != null) {
                    runRow(row);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            exception(rows.parts, e);
        }
    }

    private void initParameters(Parse headerCells) throws SQLException {
        Map<String, DbParameterAccessor> allParams =
            environment.getAllColumns(tableName);
        if (allParams.isEmpty()) {
            throw new SQLException("Cannot retrieve list of columns for " + tableName + " - check spelling and access rights");
        }
        columnBindings = new Binding[headerCells.size()];
        List<DbParameterAccessor> selectAcc = new ArrayList<DbParameterAccessor>();
        List<DbParameterAccessor> updateAcc = new ArrayList<DbParameterAccessor>();

        for (int i = 0; headerCells != null; i++, headerCells = headerCells.more) {
            String name = headerCells.text();
            String paramName = NameNormaliser.normaliseName(name);
            //need to clone db param accessors here because same column may be in the update and select part
            DbParameterAccessor orig = allParams.get(paramName);
            if (orig == null) {
                wrong(headerCells);
                throw new SQLException("Cannot find column " + paramName);
            }
            //clone parameter because there may be multiple usages of the same column
            DbParameterAccessor acc = orig.clone();
            acc.setDirection(Direction.INPUT);
            if (headerCells.text().endsWith("=")) {
                updateAcc.add(acc);
            } else {
                selectAcc.add(acc);
            }
            columnBindings[i] = new SymbolAccessSetBinding();
            columnBindings[i].adapter = new DbParameterAccessorTypeAdapter(acc, this);
        }
        // weird jdk syntax, method param is the type of array.
        selectAccessors = selectAcc.toArray(new DbParameterAccessor[0]);
        updateAccessors = updateAcc.toArray(new DbParameterAccessor[0]);
    }

    private void runRow(Parse row) throws Throwable {
        try {
            Parse cell = row.parts;
            //first set input params
            for (int column = 0; column < columnBindings.length; column++, cell = cell.more) {
                columnBindings[column].doCell(this, cell);
            }
            statement.run();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            exception(row,sqle);
            row.parts.last().more = new Parse("td", sqle.getMessage(), null, null);
        }
    }
}
