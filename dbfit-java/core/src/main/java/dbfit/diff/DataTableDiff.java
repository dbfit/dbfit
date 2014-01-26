package dbfit.diff;

import dbfit.util.MatchableDataTable;
import dbfit.util.DataTable;
import dbfit.util.DataRow;
import dbfit.util.DiffListener;
import dbfit.util.DiffResultsSummarizer;
import dbfit.util.MatchResult;
import dbfit.util.RowStructure;
import dbfit.util.DataRowProcessor;
import dbfit.util.NoMatchingRowFoundException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DataTableDiff extends DiffBase {

    private DataTable table1;
    private RowStructure rowStructure;

    public DataTableDiff(DataTable table1, RowStructure rowStructure,
                                           DiffListener listener) {
        this.table1 = table1;
        this.rowStructure = rowStructure;
        if (null != listener) {
            addListener(listener);
        }
    }

    public DataTableDiff(RowStructure rowStructure, DiffListener listener) {
        this(null, rowStructure, listener);
    }

    public DataTableDiff(RowStructure rowStructure) {
        this(null, rowStructure, null);
    }

    class DataTablesMatchProcessor implements DataRowProcessor {
        MatchableDataTable mdt2;
        public DiffResultsSummarizer summer;

        public DataTablesMatchProcessor(final DataTable table2,
                final DiffResultsSummarizer summer) {
            this.mdt2 = new MatchableDataTable(table2);
            this.summer = summer;
        }

        @Override
        public void process(DataRow row1) {
            DataRowDiff rowDiff = createChildDiff(summer);

            try {
                DataRow row2 = mdt2.findMatching(buildMatchingMask(row1));
                rowDiff.diff(row1, row2);
                mdt2.markProcessed(row2);
            } catch (NoMatchingRowFoundException nex) {
                rowDiff.diff(row1, null);
            }
        }

        public List<DataRow> getUnprocessedRows() {
            return mdt2.getUnprocessedRows();
        }
    }

    @SuppressWarnings("unchecked")
    public MatchResult<DataTable, DataTable> match(DataTable table2) {
        DiffResultsSummarizer summer = createSummer(table1, table2);

        DataTablesMatchProcessor processor = new DataTablesMatchProcessor(
                table2, summer);

        new MatchableDataTable(table1).processDataRows(processor);

        for (DataRow dr: processor.getUnprocessedRows()) {
            createChildDiff(summer).diff(null, dr);
        }

        return summer.getResult();
    }

    public MatchResult<DataTable, DataTable> diff(final DataTable table1,
                                                  final DataTable table2) {
        this.table1 = table1;
        return match(table2);
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

    private DiffResultsSummarizer createSummer(final DataTable table1,
                                               final DataTable table2) {
        return new DiffResultsSummarizer(
                MatchResult.create(table1, table2, DataTable.class),
                DataRow.class);
    }

    private DataRowDiff createChildDiff(final DiffResultsSummarizer summer) {
        DataRowDiff diff = new DataRowDiff(rowStructure.getColumnNames());
        diff.addListeners(listeners);
        diff.addListener(summer);
        return diff;
    }

}
