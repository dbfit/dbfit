package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbEnvironmentFacade;
import dbfit.api.DbParameterDescriptor;
import dbfit.util.FitNesseTestHost;
import dbfit.util.DataTable;
import dbfit.util.DataColumn;
import dbfit.util.DataRow;

import fit.Fixture;
import fit.Parse;

import java.sql.SQLException;
import java.util.Map;
import java.util.List;

import static dbfit.util.Direction.*;

public class Inspect extends fit.Fixture {
    private DbEnvironmentFacade environmentFacade;
    private String objectName;
    private String mode;
    public static String MODE_PROCEDURE = "PROCEDURE";
    public static String MODE_TABLE = "TABLE";
    public static String MODE_QUERY = "QUERY";

    public Inspect() {
        this(DbEnvironmentFactory.getDefaultEnvironment(), null, null);
    }

    public Inspect(DBEnvironment dbEnvironment, String mode, String objName) {
        this.objectName = objName;
        this.mode = mode;
        this.environmentFacade =
            new DbEnvironmentFacade(dbEnvironment, FitNesseTestHost.getInstance());
    }

    public void doTable(Parse table) {
        if (objectName == null) {
            objectName = args[0];
        }

        try {
            if (MODE_PROCEDURE.equalsIgnoreCase(mode)) {
                inspectProcedure(table);
            } else if (MODE_TABLE.equalsIgnoreCase(mode)) {
                inspectTable(table);
            } else if (MODE_QUERY.equalsIgnoreCase(mode)) {
                inspectQuery(table);
            } else {
                throw new Exception("Unknown inspect mode " + mode);
            }
        } catch (Exception e) {
            exception(table.parts.parts, e);
        }
    }

    private void inspectTable(Parse table) throws SQLException {
        Map<String, ? extends DbParameterDescriptor> allParams =
            environmentFacade.describeProcedureParameters(objectName);
        if (allParams.isEmpty()) {
            throw new SQLException(
                    "Cannot retrieve list of columns for table or view " +
                    objectName + " - check spelling and access rights");
        }
        addRowWithParamNames(table, allParams);
    }

    private void inspectProcedure(Parse table) throws SQLException {
        Map<String, ? extends DbParameterDescriptor> allParams =
            environmentFacade.describeProcedureParameters(objectName);
        if (allParams.isEmpty()){
            throw new SQLException(
                    "Cannot retrieve list of parameters for procedure " +
                    objectName + " - check spelling and access rights");
        }
        addRowWithParamNames(table, allParams);
    }

    private void inspectQuery(Parse table) throws SQLException {
        DataTable dt = environmentFacade.getQueryTable(objectName);
        Parse newRow = getHeaderFromTableColumns(dt.getColumns());
        table.parts.more = newRow;

        for (DataRow row : dt.getRows()) {
            newRow.more = getDataRow(row, dt.getColumns());
            newRow = newRow.more;
        }
    }

    private Parse getDataRow(DataRow row, List<DataColumn> columns) {
        Parse newRow = new Parse("tr", null, null, null);
        Parse prevCell = null;

        for (DataColumn column : columns) {
            Parse cell = new Parse("td",
                Fixture.gray(row.getStringValue(column.getName())), null, null);
            if (prevCell == null) {
                newRow.parts = cell;
            } else {
                prevCell.more = cell;
            }
            prevCell = cell;
        }

        return newRow;
    }

    private Parse getHeaderFromTableColumns(List<DataColumn> columns) {
        Parse newRow = new Parse("tr", null, null, null);
        Parse prevCell = null;

        for (DataColumn column : columns) {
            Parse cell = new Parse("td", Fixture.gray(column.getName()), null, null);
            if (prevCell == null) {
                newRow.parts = cell;
            } else {
                prevCell.more = cell;
            }
            prevCell = cell;
        }

        return newRow;
    }

    private void addRowWithParamNames(Parse table, Map<String, ? extends DbParameterDescriptor> params) {
        Parse newRow = new Parse("tr", null, null, null);
        table.parts.more = newRow;
        Parse prevCell = null;
        String orderedNames[] = new String[params.size()];

        for (String s : params.keySet()) {
            if (params.get(s).getPosition() == -1) {
                orderedNames[0] = s;
            } else {
                orderedNames[params.get(s).getPosition()] = s;
            }
        }

        for (int i = 0; i < orderedNames.length; i++) {
            String name = orderedNames[i];

            if (name == null) {
                name = "";
            }

            if (params.get(name).getDirection() != INPUT) {
                name = name + "?";
            }

            Parse cell = new Parse("td", Fixture.gray(name), null, null);
            if (prevCell == null) {
                newRow.parts = cell;
            } else {
                prevCell.more = cell;
            }
            prevCell = cell;
        }
    }
}
