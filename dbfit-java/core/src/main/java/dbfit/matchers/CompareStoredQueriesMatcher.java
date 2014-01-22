package dbfit.matchers;

import dbfit.util.MatchableDataTable;
import dbfit.util.MatchableDataRow;
import dbfit.util.MatchableDataCell;
import dbfit.util.DiffListener;
import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.RowStructure;
import dbfit.util.DataRowProcessor;
import dbfit.util.DataRow;
import dbfit.util.NoMatchingRowFoundException;
import static dbfit.util.MatchStatus.*;

import java.util.Map;
import java.util.HashMap;

public class CompareStoredQueriesMatcher {

    private MatchableDataTable table1;
    private DiffListener listener;
    private RowStructure rowStructure;

    class DataTablesMatchProcessor implements DataRowProcessor {
        private MatchableDataTable table2;
        public MatchResult<MatchableDataTable, MatchableDataTable> result;

        public DataTablesMatchProcessor(MatchableDataTable table2,
                MatchResult<MatchableDataTable, MatchableDataTable> result) {
            this.table2 = table2;
            this.result = result;
        }

        @Override
        public void process(DataRow row1) {
            MatchResult<MatchableDataRow, MatchableDataRow> rowResult;
            try {
                DataRow row2 = table2.findMatching(buildMatchingMask(row1));
                table2.markProcessed(row2);
                rowResult = addRow(row1, row2, table2);
            } catch (NoMatchingRowFoundException nex) {
                rowResult = addMissingRow(row1, table2);
            }

            if (!rowResult.isMatching()) {
                result.setStatus(MatchStatus.WRONG);
            }
        }
    }

    public CompareStoredQueriesMatcher(MatchableDataTable table1,
            RowStructure rowStructure, DiffListener listener) {
        this.table1 = table1;
        this.rowStructure = rowStructure;
        this.listener = listener;
    }

    public void setListener(DiffListener listener) {
        this.listener = listener;
    }

    public MatchResult<MatchableDataTable, MatchableDataTable> match(
            MatchableDataTable table2) {
        MatchResult<MatchableDataTable, MatchableDataTable> tableResult =
                MatchResult.create(table1, table2, SUCCESS);
        DataRowProcessor processor = new DataTablesMatchProcessor(table2, tableResult);
        table1.processDataRows(processor);

        for (DataRow dr: table2.getUnprocessedRows()) {
            addSurplusRow(dr, table2);
            tableResult.setStatus(WRONG);
        }

        return tableResult;
    }

    private MatchResult<MatchableDataRow, MatchableDataRow> addMissingRow(
            DataRow dr1, MatchableDataTable table2) {
        return addRow(dr1, null, table2);
    }

    private MatchResult<MatchableDataRow, MatchableDataRow> addSurplusRow(
            DataRow dr2, MatchableDataTable table2) {
        return addRow(null, dr2, table2);
    }

    private MatchResult<MatchableDataRow, MatchableDataRow> addRow(
            DataRow dr1, DataRow dr2, MatchableDataTable table2) {
        MatchableDataRow mdr1 = buildMatchableDataRow(dr1, table1.getName());
        MatchableDataRow mdr2 = buildMatchableDataRow(dr2, table2.getName());

        MatchResult<MatchableDataRow, MatchableDataRow> rowResult =
                MatchResult.create(mdr1, mdr2, MatchStatus.SUCCESS);

        try {
            for (String columnName: rowStructure.getColumnNames()) {
                matchCell(mdr1, mdr2, columnName, rowResult);
            }
        } catch (Exception e) {
            rowResult.setException(e);
        } finally {
            listener.endRow(rowResult);
        }

        return rowResult;
    }

    public void matchCell(MatchableDataRow mdr1, MatchableDataRow mdr2,
            String columnName,
            MatchResult<MatchableDataRow, MatchableDataRow> rowResult) {
        matchCell(
                buildMatchableDataCell(mdr1, columnName),
                buildMatchableDataCell(mdr2, columnName), rowResult);
    }

    public void matchCell(MatchableDataCell c1, MatchableDataCell c2,
            MatchResult<MatchableDataRow, MatchableDataRow> rowResult) {
        if (c1 == null) {
            rowResult.setStatus(MatchStatus.SURPLUS);
            listener.endCell(MatchResult.create(c1, c2, MatchStatus.SURPLUS));
        } else if (c2 == null) {
            rowResult.setStatus(MatchStatus.MISSING);
            listener.endCell(MatchResult.create(c1, c2, MatchStatus.MISSING));
        } else if (!compare(c1, c2)) {
            rowResult.setStatus(MatchStatus.WRONG);
            listener.endCell(MatchResult.create(c1, c2, MatchStatus.WRONG));
        } else {
            listener.endCell(MatchResult.create(c1, c2, MatchStatus.SUCCESS));
        }
    }

    public boolean compare(MatchableDataCell c1, MatchableDataCell c2) {
        String lval = c1.getStringValue();
        String rval = c2.getStringValue();
        return lval.equals(rval);
    }

    public Map<String, Object> buildMatchingMask(final DataRow dr) {
        final Map<String, Object> matchingMask = new HashMap<String, Object>();
        for (int i = 0; i < rowStructure.size(); i++) {
            addToMask(i, matchingMask, dr);
        }

        return matchingMask;
    }

    private void addToMask(int index, final Map<String, Object> mask, DataRow dr) {
        if (rowStructure.isKeyColumn(index)) {
            String columnName = rowStructure.getColumnName(index);
            mask.put(columnName, dr.get(columnName));
        }
    }

    private String getColumnName(int index) {
        return rowStructure.getColumnName(index);
    }

    private MatchableDataRow buildMatchableDataRow(DataRow dr, String name) {
        return (dr == null) ? null : new MatchableDataRow(dr, name);
    }

    private MatchableDataCell buildMatchableDataCell(MatchableDataRow mdr,
            String columnName) {
        return (mdr == null) ? null : new MatchableDataCell(mdr, columnName);
    }
}

