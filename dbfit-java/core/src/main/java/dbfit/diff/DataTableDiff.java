package dbfit.diff;

import dbfit.util.MatchableDataTable;
import dbfit.util.MatchableDataRow;
import dbfit.util.DiffListener;
import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.RowStructure;
import dbfit.util.DataRowProcessor;
import dbfit.util.DataRow;
import dbfit.util.DataCell;
import dbfit.util.NoMatchingRowFoundException;
import static dbfit.util.MatchStatus.*;
import static dbfit.util.DataCell.createDataCell;

import java.util.Map;
import java.util.HashMap;

public class DataTableDiff {

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
                rowResult = addRow(row1, null, table2);
            }

            if (!rowResult.isMatching()) {
                result.setStatus(MatchStatus.WRONG);
            }
        }
    }

    public DataTableDiff(MatchableDataTable table1,
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
            addRow(null, dr, table2);
            tableResult.setStatus(WRONG);
        }

        return tableResult;
    }

    private DiffListener createCellListener(
            final MatchResult<MatchableDataRow, MatchableDataRow> rowResult) {
        return new DiffListener() {
            @Override
            public void endRow(MatchResult<MatchableDataRow, MatchableDataRow> result) {
                // ignore
            }

            @Override
            public void endCell(MatchResult<DataCell, DataCell> result) {
                switch (result.getStatus()) {
                case WRONG:
                case SURPLUS:
                case MISSING:
                case EXCEPTION:
                    rowResult.setStatus(result.getStatus());
                    break;
                }
            }
        };
    }

    private DataCellDiff createDataCellDiff(
            final MatchResult<MatchableDataRow, MatchableDataRow> rowResult) {
        DataCellDiff diff = new DataCellDiff();
        diff.addListener(listener);
        diff.addListener(createCellListener(rowResult));
        return diff;
    }

    private MatchResult<MatchableDataRow, MatchableDataRow> addRow(
            DataRow dr1, DataRow dr2, MatchableDataTable table2) {

        MatchableDataRow mdr1 = buildMatchableDataRow(dr1, table1.getName());
        MatchableDataRow mdr2 = buildMatchableDataRow(dr2, table2.getName());

        MatchResult<MatchableDataRow, MatchableDataRow> rowResult =
                MatchResult.create(mdr1, mdr2, MatchStatus.SUCCESS);

        try {
            for (String column: rowStructure.getColumnNames()) {
                createDataCellDiff(rowResult).diff(
                            createDataCell(dr1, column),
                            createDataCell(dr2, column));
            }
        } catch (Exception e) {
            rowResult.setException(e);
        } finally {
            listener.endRow(rowResult);
        }

        return rowResult;
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

    private MatchableDataRow buildMatchableDataRow(DataRow dr, String name) {
        return (dr == null) ? null : new MatchableDataRow(dr, name);
    }

}
