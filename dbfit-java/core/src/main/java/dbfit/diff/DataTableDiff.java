package dbfit.diff;

import dbfit.util.MatchableDataTable;
import dbfit.util.DataTable;
import dbfit.util.DataRow;
import dbfit.util.RowStructure;
import dbfit.util.DataRowProcessor;
import dbfit.util.NoMatchingRowFoundException;
import dbfit.util.MatchingMaskBuilder;

public class DataTableDiff extends CompositeDiff<DataTable, DataRow> {

    private RowStructure rowStructure;

    public DataTableDiff(RowStructure rowStructure) {
        this.rowStructure = rowStructure;
    }

    @Override
    protected Class getType() {
        return DataTable.class;
    }

    @Override
    protected Class getChildType() {
        return DataRow.class;
    }

    @Override
    DiffRunner getDiffRunner(final DataTable dr1, final DataTable dr2) {
        return new DataTableDiffRunner(dr1, dr2);
    }

    class DataTableDiffRunner extends CompositeDiffRunner implements DataRowProcessor {

        private MatchingMaskBuilder mmb = new MatchingMaskBuilder(rowStructure);
        private MatchableDataTable mdt2;

        public DataTableDiffRunner(final DataTable table1, final DataTable table2) {
            super(table1, table2);
            this.mdt2 = new MatchableDataTable(table2);
        }

        @Override
        protected DataRowDiff newChildDiff() {
            return new DataRowDiff(rowStructure.getColumnNames());
        }

        @Override
        protected void uncheckedDiff() {
            new MatchableDataTable(o1).processDataRows(this);

            for (DataRow dr: mdt2.getUnprocessedRows()) {
                createChildDiff().diff(null, dr);
            }
        }

        @Override
        public void process(DataRow row1) {
            DiffBase<DataRow, DataRow> rowDiff = createChildDiff();

            try {
                DataRow row2 = mdt2.findMatching(mmb.buildMatchingMask(row1));
                rowDiff.diff(row1, row2);
                mdt2.markProcessed(row2);
            } catch (NoMatchingRowFoundException nex) {
                rowDiff.diff(row1, null);
            }
        }

    }

}
