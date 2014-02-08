package dbfit.diff;

import dbfit.util.DataRow;
import dbfit.util.DataCell;
import dbfit.util.MatchResult;
import static dbfit.util.DataCell.createDataCell;

public class DataRowDiff extends CompositeDiff<DataRow, DataCell> {
    private String[] columnNames;

    public DataRowDiff(final String[] columnNames) {
        this(columnNames, new DataCellDiff());
    }

    public DataRowDiff(final String[] columnNames, final DataCellDiff cellDiff) {
        super(cellDiff);
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
    protected DiffRunner getDiffRunner(MatchResult<DataRow, DataRow> request) {
        return new DataRowDiffRunner(request);
    }

    class DataRowDiffRunner extends CompositeDiffRunner {
        public DataRowDiffRunner(MatchResult<DataRow, DataRow> request) {
            super(request);
        }

        @Override
        protected void uncheckedDiff() {
            for (String column: columnNames) {
                getChildDiff().diff(
                            createDataCell(obj1, column),
                            createDataCell(obj2, column));
            }
        }
    }

}
