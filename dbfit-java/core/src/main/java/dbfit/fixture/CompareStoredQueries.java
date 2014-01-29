package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.util.*;
import dbfit.fixture.report.ReportingSystem;
import dbfit.fixture.report.FitFixtureReportingSystem;
import static dbfit.util.DataCell.createDataCell;
import static dbfit.util.MatchStatus.*;

import fit.Fixture;
import fit.Parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompareStoredQueries extends fit.Fixture {
    private String symbol1;
    private String symbol2;
    private MatchableDataTable dt1;
    private MatchableDataTable dt2;
    protected String[] columnNames;
    private boolean[] keyProperties;
    protected ReportingSystem reportingSystem;

    public CompareStoredQueries() {
    }

    public CompareStoredQueries(DBEnvironment environment, String symbol1, String symbol2) {
        this.symbol1 = symbol1;
        this.symbol2 = symbol2;
    }

    private void initialiseDataTables() {
        if (symbol1 == null || symbol2 == null) {
            if (args.length < 2)
                throw new UnsupportedOperationException("No symbols specified to CompareStoreQueries constructor or argument list");
            symbol1 = args[0];
            symbol2 = args[1];
        }
        dt1 = new MatchableDataTable(SymbolUtil.getDataTable(symbol1));
        dt2 = new MatchableDataTable(SymbolUtil.getDataTable(symbol2));
    }

    public void doTable(Parse table) {
        initialiseDataTables();
        Parse lastRow = table.parts.more;
        if (lastRow == null) {
            throw new Error("Query structure missing from second row");
        }

        reportingSystem = new FitFixtureReportingSystem(this, table);

        loadRowStructure(lastRow);
        lastRow = processDataTable(dt1, dt2, lastRow, symbol2);

        List<DataRow> unproc = dt2.getUnprocessedRows();
        for (DataRow dr : unproc) {
            Parse errorRow = parseDataRowAsError(dr, " missing from " + symbol1, true);
            // lastRow.more = errorRow;
            // lastRow = errorRow;
        }
    }

    private void loadRowStructure(Parse headerRow) {
        Parse headerCell = headerRow.parts;
        int colNum = headerRow.parts.size();
        columnNames = new String[colNum];
        keyProperties = new boolean[colNum];
        for (int i = 0; i < colNum; i++) {
            String currentName = headerCell.text();
            if (currentName == null) throw new UnsupportedOperationException("Column " + i + " does not have a name");
            currentName = currentName.trim();
            if (currentName.length() == 0)
                throw new UnsupportedOperationException("Column " + i + " does not have a name");
            columnNames[i] = NameNormaliser.normaliseName(currentName);
            keyProperties[i] = !currentName.endsWith("?");
            headerCell = headerCell.more;
        }
    }

    protected Map<String, Object> buildMatchingMask(final DataRow dr) {
        final Map<String, Object> matchingMask = new HashMap<String, Object>();
        for (int i = 0; i < keyProperties.length; i++) {
            if (keyProperties[i])
                matchingMask.put(columnNames[i], dr.get(columnNames[i]));
        }

        return matchingMask;
    }

    protected Parse processDataTable(final MatchableDataTable t1, final MatchableDataTable t2, final Parse lastScreenRow, final String queryName) {
        class DataTablesMatchProcessor implements DataRowProcessor {
            Parse screenRow = lastScreenRow;

            public void process(DataRow dr) {
                Parse newRow = null;
                try {
                    DataRow dr2 = t2.findMatching(buildMatchingMask(dr));
                    t2.markProcessed(dr2);
                    newRow = parseDataRows(dr, dr2);
                } catch (NoMatchingRowFoundException nex) {
                    newRow = parseDataRowAsError(dr, " missing from " + queryName, false);
                }
                // screenRow.more = newRow;
                screenRow = newRow;
            }
        }

        DataTablesMatchProcessor processor = new DataTablesMatchProcessor();
        t1.processDataRows(processor);
        return processor.screenRow;
    }

    protected Parse parseDataRows(DataRow dr, DataRow dr2) {
        Parse newRow = new Parse("tr", null, null, null);
        MatchResult rowRes = MatchResult.create(dr, dr2, DataRow.class);
        try {
            String lval = dr.getStringValue(columnNames[0]);
            String rval = dr2.getStringValue(columnNames[0]);
            DataCell dc1 = createDataCell(dr, columnNames[0]);
            DataCell dc2 = createDataCell(dr2, columnNames[0]);
            MatchResult cellRes = MatchResult.create(dc1, dc2, DataCell.class);

            // Parse firstCell = new Parse("td", lval, null, null);
            // newRow.parts = firstCell;

            if (!lval.equals(rval)) {
                // wrong(firstCell, rval);
                cellRes.setStatus(WRONG);
                rowRes.setStatus(WRONG);
            } else {
                // right(firstCell);
                cellRes.setStatus(SUCCESS);
            }

            reportingSystem.addCell(cellRes);

            for (int i = 1; i < columnNames.length; i++) {
                lval = dr.getStringValue(columnNames[i]);
                rval = dr2.getStringValue(columnNames[i]);

                dc1 = createDataCell(dr, columnNames[i]);
                dc2 = createDataCell(dr2, columnNames[i]);
                cellRes = MatchResult.create(dc1, dc2, DataCell.class);

                // Parse nextCell = new Parse("td",
                //        lval, null, null);
                // firstCell.more = nextCell;
                // firstCell = nextCell;
                if (!lval.equals(rval)) {
                    // wrong(firstCell, rval);
                    cellRes.setStatus(WRONG);
                } else {
                    // right(firstCell);
                    cellRes.setStatus(SUCCESS);
                }

                reportingSystem.addCell(cellRes);
            }

            reportingSystem.endRow(rowRes);
        } catch (Exception e) {
            // exception(newRow, e);
            rowRes.setException(e);
            reportingSystem.addException(e);
        }
        return newRow;
    }

    protected Parse parseDataRowAsError(DataRow dr, String desc,
            boolean leftSideMissing) {
        Parse newRow = new Parse("tr", null, null, null);
        DataRow dr1 = (leftSideMissing) ? null : dr;
        DataRow dr2 = (leftSideMissing) ? dr : null;
        MatchStatus status = (leftSideMissing) ? SURPLUS : MISSING;
        MatchResult rowRes = MatchResult.create(dr1, dr2, status, DataRow.class);
        String msg = "missing from " + (leftSideMissing ? symbol1 : symbol2);

        // MatchResult rowRes = MatchResult.create(dr, dr2, DataRow.class);
        try {
            // Parse firstCell = new Parse("td",
            //        dr.getStringValue(columnNames[0]), null, null);
            DataCell dc1 = createDataCell(dr1, columnNames[0]);
            DataCell dc2 = createDataCell(dr2, columnNames[0]);
            MatchResult cellRes =
                MatchResult.create(dc1, dc2, status, DataCell.class);
            // newRow.parts = firstCell;
            // firstCell.addToBody(Fixture.gray(desc));
            // wrong(firstCell);
            reportingSystem.addCell(cellRes);

            for (int i = 1; i < columnNames.length; i++) {
                // Parse nextCell = new Parse("td",
                //        dr.getStringValue(columnNames[i]), null, null);
                // firstCell.more = nextCell;
                // firstCell = nextCell;
                dc1 = createDataCell(dr1, columnNames[i]);
                dc2 = createDataCell(dr2, columnNames[i]);
                cellRes = MatchResult.create(dc1, dc2, status, DataCell.class);
                reportingSystem.addCell(cellRes);
            }

            reportingSystem.endRow(rowRes, msg);
        } catch (Exception e) {
            // exception(newRow, e);
            rowRes.setException(e);
            reportingSystem.addException(e);
        }

        return newRow;
    }

    public static class FitFixtureReporter extends NoOpDiffListenerAdapter {
        private ReportingSystem reportingSystem;

        public FitFixtureReporter(final ReportingSystem reportingSystem) {
            this.reportingSystem = reportingSystem;
        }

        @Override
        public void endRow(MatchResult<DataRow, DataRow> result) {
            throw new UnsupportedOperationException("Not implemented yet");
        }

        @Override
        public void endCell(MatchResult<DataCell, DataCell> result) {
            reportingSystem.addCell(result);
        }
    }
}

