package dbfit.diff;

import dbfit.util.MatchableDataTable;
import dbfit.util.DataTable;
import dbfit.util.DataRow;
import dbfit.util.DiffResultsSummarizer;
import dbfit.util.MatchResult;
import dbfit.util.RowStructure;
import dbfit.util.DataRowProcessor;
import dbfit.util.NoMatchingRowFoundException;
import dbfit.util.MatchingMaskBuilder;

import java.util.List;

public class DataTableDiff extends DiffBase {

    private RowStructure rowStructure;

    public DataTableDiff(RowStructure rowStructure) {
        this.rowStructure = rowStructure;
    }

    class DataTablesMatchProcessor implements DataRowProcessor {
        MatchableDataTable mdt2;
        public DiffResultsSummarizer summer;
        private MatchingMaskBuilder mmb = new MatchingMaskBuilder(rowStructure);

        public DataTablesMatchProcessor(
                final DataTable table2,
                final DiffResultsSummarizer summer) {
            this.mdt2 = new MatchableDataTable(table2);
            this.summer = summer;
        }

        @Override
        public void process(DataRow row1) {
            DataRowDiff rowDiff = createChildDiff(summer);

            try {
                DataRow row2 = mdt2.findMatching(mmb.buildMatchingMask(row1));
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

    public void diff(final DataTable table1, final DataTable table2) {
        new DataTableDiffRunner(table1, table2).runDiff();
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

    class DataTableDiffRunner extends DiffRunner {
        private final DataTable table1;
        private final DataTable table2;
        private final DiffResultsSummarizer summer;

        public DataTableDiffRunner(final DataTable table1, final DataTable table2) {
            this.table1 = table1;
            this.table2 = table2;
            this.summer = createSummer(table1, table2);
        }

        @Override public MatchResult getResult() {
            return summer.getResult();
        }

        @Override
        protected void uncheckedDiff() {
            DataTablesMatchProcessor processor = new DataTablesMatchProcessor(
                    table2, summer);

            new MatchableDataTable(table1).processDataRows(processor);

            for (DataRow dr: processor.getUnprocessedRows()) {
                createChildDiff(summer).diff(null, dr);
            }
        }
    }

}
