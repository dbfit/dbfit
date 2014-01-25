package dbfit.diff;

import dbfit.util.MatchableDataTable;
import dbfit.util.DataTable;
import dbfit.util.DiffListener;
import dbfit.util.NoOpDiffListenerAdapter;
import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.RowStructure;
import dbfit.util.DataRowProcessor;
import dbfit.util.DataRow;
import dbfit.util.DataCell;
import dbfit.util.NoMatchingRowFoundException;
import static dbfit.util.MatchStatus.*;
import static dbfit.util.DataCell.createDataCell;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DataTableDiff {

    private DataTable table1;
    private DiffListener listener;
    private RowStructure rowStructure;

    public DataTableDiff(DataTable table1,
            RowStructure rowStructure, DiffListener listener) {
        this.table1 = table1;
        this.rowStructure = rowStructure;
        this.listener = listener;
    }

    class DataTablesMatchProcessor implements DataRowProcessor {
        MatchableDataTable mdt2;
        public MatchResult<DataTable, DataTable> result;

        public DataTablesMatchProcessor(DataTable table2,
                MatchResult<DataTable, DataTable> result) {
            this.mdt2 = new MatchableDataTable(table2);
            this.result = result;
        }

        private DiffListener createRowListener() {
            return new NoOpDiffListenerAdapter() {
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
            };
        }

        @Override
        public void process(DataRow row1) {
            MatchResult<DataRow, DataRow> rowResult;
            DataRowDiff rowDiff = createDataRowDiff();
            rowDiff.addListener(createRowListener());

            try {
                DataRow row2 = mdt2.findMatching(buildMatchingMask(row1));
                rowDiff.diff(row1, row2);
                mdt2.markProcessed(row2);
            } catch (NoMatchingRowFoundException nex) {
                rowDiff.diff(row1, null);
                result.setStatus(WRONG);
            }
        }

        public List<DataRow> getUnprocessedRows() {
            return mdt2.getUnprocessedRows();
        }
    }

    public void setListener(DiffListener listener) {
        this.listener = listener;
    }

    public MatchResult<DataTable, DataTable> match(DataTable table2) {
        MatchResult<DataTable, DataTable> tableResult =
                MatchResult.create(table1, table2, SUCCESS, DataTable.class);

        DataTablesMatchProcessor processor = new DataTablesMatchProcessor(
                table2, tableResult);

        new MatchableDataTable(table1).processDataRows(processor);

        for (DataRow dr: processor.getUnprocessedRows()) {
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
