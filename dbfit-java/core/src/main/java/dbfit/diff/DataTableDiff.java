package dbfit.diff;

import dbfit.util.DefaultDataTableProcessor;
import dbfit.util.MatchableDataTable;
import dbfit.util.DataTable;
import dbfit.util.DataRow;
import dbfit.util.MatchResult;
import dbfit.util.RowStructure;
import dbfit.util.DataRowProcessor;
import dbfit.util.MatchingMaskBuilder;

public class DataTableDiff extends CompositeDiff<DataTable, DataRow> {

    private RowStructure rowStructure;

    public DataTableDiff(RowStructure rowStructure) {
        this(rowStructure, new DataRowDiff(rowStructure.getColumnNames()));
    }

    public DataTableDiff(RowStructure rowStructure, DataRowDiff rowDiff) {
        super(rowDiff);
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
    protected DiffRunner getDiffRunner(MatchResult<DataTable, DataTable> request) {
        return new DataTableDiffRunner(request);
    }

    class DataTableDiffRunner extends CompositeDiffRunner implements DataRowProcessor {

        private MatchingMaskBuilder mmb = new MatchingMaskBuilder(rowStructure);
        private MatchableDataTable mdt2;

        public DataTableDiffRunner(MatchResult<DataTable, DataTable> request) {
            super(request);
            this.mdt2 = new MatchableDataTable(obj2);
        }

        @Override
        protected void uncheckedDiff() {
            new DefaultDataTableProcessor(this).process(obj1);

            for (DataRow dr: mdt2.getUnprocessedRows()) {
                getChildDiff().diff(null, dr);
            }
        }

        @Override
        public void process(final DataRow row1) {
            DataRow row2 = mdt2.findMatchingNothrow(mmb.buildMatchingMask(row1));
            getChildDiff().diff(row1, row2);
            mdt2.markProcessed(row2);
        }
    }
}
