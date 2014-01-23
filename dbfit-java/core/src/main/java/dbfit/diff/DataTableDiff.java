package dbfit.diff;

import dbfit.util.MatchableDataTable;
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

    public DataTableDiff(MatchableDataTable table1,
            RowStructure rowStructure, DiffListener listener) {
        this.table1 = table1;
        this.rowStructure = rowStructure;
        this.listener = listener;
    }

    class DataTablesMatchProcessor implements DataRowProcessor {
        private MatchableDataTable table2;
        public MatchResult<MatchableDataTable, MatchableDataTable> result;

        public DataTablesMatchProcessor(MatchableDataTable table2,
                MatchResult<MatchableDataTable, MatchableDataTable> result) {
            this.table2 = table2;
            this.result = result;
        }

        private DiffListener createRowListener() {
            return new DiffListener() {
                @Override
                public void endRow(MatchResult<DataRow, DataRow> rowResult) {
                    switch (rowResult.getStatus()) {
                    case WRONG:
                    case SURPLUS:
                    case MISSING:
                    case EXCEPTION:
                        result.setStatus(rowResult.getStatus());
                        break;
                    }
                }

                @Override
                public void endCell(MatchResult<DataCell, DataCell> cellResult) {
                    // ignore
                }
            };
        }

        @Override
        public void process(DataRow row1) {
            MatchResult<DataRow, DataRow> rowResult;
            DataRowDiff rowDiff = createDataRowDiff();
            rowDiff.addListener(createRowListener());

            try {
                DataRow row2 = table2.findMatching(buildMatchingMask(row1));
                rowDiff.diff(row1, row2);
                table2.markProcessed(row2);
            } catch (NoMatchingRowFoundException nex) {
                rowDiff.diff(row1, null);
                result.setStatus(WRONG);
            }
        }
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
            createDataRowDiff().diff(null, dr);
            tableResult.setStatus(WRONG);
        }

        return tableResult;
    }

    private DataRowDiff createDataRowDiff() {
        DataRowDiff diff = new DataRowDiff(rowStructure.getColumnNames());
        diff.addListener(listener);
        return diff;
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

}
