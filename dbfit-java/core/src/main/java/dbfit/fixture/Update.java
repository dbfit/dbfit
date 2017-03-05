package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbCommand;
import dbfit.api.DbUpdateCommandBuilder;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessorTypeAdapter;
import dbfit.util.NameNormaliser;
import dbfit.util.SymbolAccessSetBinding;

import fit.Binding;
import fit.Parse;

import java.sql.SQLException;

public class Update extends fit.Fixture {
    private DBEnvironment environment;
    private String tableName;
    private Binding[] columnBindings;

    public Update() {
        this(DbEnvironmentFactory.getDefaultEnvironment());
    }

    public Update(DBEnvironment dbEnvironment) {
        this(dbEnvironment, null);
    }

    public Update(DBEnvironment dbEnvironment, String tableName) {
        this.tableName = tableName;
        this.environment = dbEnvironment;
    }

    public void doRows(Parse rows) {
        // if table not defined as parameter, read from fixture argument; if still not defined, read from first row
        if ((tableName == null || tableName.trim().length() == 0) && args.length > 0) {
            tableName = args[0];
        } else if (tableName == null) {
            tableName = rows.parts.text();
            rows = rows.more;
        }

        // init parameters from the first row
        try (DbCommand statement = buildUpdateCommand(rows.parts)) {
            Parse row = rows;
            while ((row = row.more) != null) {
                runRow(row, statement);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            exception(rows.parts, e);
        }
    }

    private DbCommand buildUpdateCommand(Parse headerCells) {
        try {
            DbUpdateCommandBuilder updateBuilder =
                new DbUpdateCommandBuilder(environment, tableName);
            columnBindings = new Binding[headerCells.size()];

            for (int i = 0; headerCells != null; i++, headerCells = headerCells.more) {
                DbParameterAccessor acc = addParameter(headerCells.text(), updateBuilder);
                initColumnBinding(i, acc);
            }

            return updateBuilder.build();
        } catch (Exception e) {
            wrong(headerCells);
            throw e;
        }
    }

    private DbParameterAccessor addParameter(String name, DbUpdateCommandBuilder updateBuilder) {
        String paramName = NameNormaliser.normaliseName(name);

        if (name.endsWith("=")) {
            return updateBuilder.addUpdateAccessor(paramName);
        } else {
            return updateBuilder.addSelectAccessor(paramName);
        }
    }

    private void initColumnBinding(int column, DbParameterAccessor accessor) {
        columnBindings[column] = new SymbolAccessSetBinding();
        columnBindings[column].adapter = new DbParameterAccessorTypeAdapter(accessor, this);
    }

    private void bindRowValues(Parse row) throws Throwable {
        Parse cell = row.parts;
        for (int column = 0; column < columnBindings.length; column++, cell = cell.more) {
            columnBindings[column].doCell(this, cell);
        }
    }

    private void runRow(Parse row, DbCommand statement) throws Throwable {
        try {
            bindRowValues(row);
            statement.execute();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            exception(row,sqle);
            row.parts.last().more = new Parse("td", sqle.getMessage(), null, null);
        }
    }
}
