package dbfit.diff;

import dbfit.util.DataRow;
import dbfit.util.DataCell;
import static dbfit.util.DataCell.createDataCell;

public class DataRowDiff extends CompositeDiff<DataRow, DataCell> {
    private String[] columnNames;

    public DataRowDiff(final String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    protected Class getType() {
        return DataRow.class;
    }

    @Override
    protected Class getChildType() {
        return DataCell.class;
    }

    @Override
    DiffRunner getDiffRunner(final DataRow dr1, final DataRow dr2) {
        return new DataRowDiffRunner(dr1, dr2);
    }

    class DataRowDiffRunner extends CompositeDiffRunner {
        public DataRowDiffRunner(final DataRow dr1, final DataRow dr2) {
            super(dr1, dr2);
        }

        @Override
        protected DataCellDiff newChildDiff() {
            return new DataCellDiff();
        }

        @Override
        protected void uncheckedDiff() {
            for (String column: columnNames) {
                createChildDiff().diff(
                            createDataCell(o1, column),
                            createDataCell(o2, column));
            }
        }
    }

}
