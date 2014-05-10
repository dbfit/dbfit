package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.DbParameterAccessor;
import dbfit.util.FitNesseTestHost;
import fit.Fixture;
import fit.Parse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import static dbfit.util.Direction.*;

public class Inspect extends fit.Fixture {
    private DBEnvironment environment;
    private String objectName;
    private String mode;
    public static String MODE_PROCEDURE = "PROCEDURE";
    public static String MODE_TABLE = "TABLE";
    public static String MODE_QUERY = "QUERY";

    public Inspect() {
        this.environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public Inspect(DBEnvironment dbEnvironment, String mode, String objName) {
        this.objectName = objName;
        this.mode = mode;
        this.environment = dbEnvironment;
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
        Map<String, DbParameterAccessor> allParams =
            environment.getAllColumns(objectName);
        if (allParams.isEmpty()) {
            throw new SQLException(
                    "Cannot retrieve list of columns for table or view " +
                    objectName + " - check spelling and access rights");
        }
        addRowWithParamNames(table, allParams);
    }

    private void inspectProcedure(Parse table) throws SQLException {
        Map<String, DbParameterAccessor> allParams =
            environment.getAllProcedureParameters(objectName);
        if (allParams.isEmpty()){
            throw new SQLException(
                    "Cannot retrieve list of parameters for procedure " +
                    objectName + " - check spelling and access rights");
        }
        addRowWithParamNames(table, allParams);
    }

    private void inspectQuery(Parse table) throws SQLException {
        try (PreparedStatement st =
                environment.createStatementWithBoundFixtureSymbols(
                    FitNesseTestHost.getInstance(), objectName)) {
            ResultSet rs = st.executeQuery();
            Parse newRow = getHeaderFromRS(rs);
            table.parts.more = newRow;
            while (rs.next()) {
                newRow.more = getDataRow(rs);
                newRow = newRow.more;
            }
            rs.close();
        }
    }

    private Parse getDataRow(ResultSet rs) throws SQLException {
        Parse newRow = new Parse("tr", null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
        Parse prevCell = null;
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            Object value = rs.getObject(i + 1);
            value = DbParameterAccessor.normaliseValue(value);
            Parse cell = new Parse("td",
                Fixture.gray(value == null ? "null" : value.toString()), null, null);
            if (prevCell == null) {
                newRow.parts = cell;
            } else {
                prevCell.more = cell;
            }
            prevCell = cell;
        }
        return newRow;
    }

    private Parse getHeaderFromRS(ResultSet rs) throws SQLException {
        Parse newRow = new Parse("tr", null, null, null);
        ResultSetMetaData rsmd = rs.getMetaData();
        Parse prevCell = null;
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            Parse cell = new Parse("td", Fixture.gray(rsmd.getColumnLabel(i + 1)), null, null);
            if (prevCell == null) {
                newRow.parts = cell;
            } else {
                prevCell.more = cell;
            }
            prevCell = cell;
        }
        return newRow;
    }

    private void addRowWithParamNames(Parse table, Map<String, DbParameterAccessor> params) {
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

            if (params.get(name).doesNotHaveDirection(INPUT)) {
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
