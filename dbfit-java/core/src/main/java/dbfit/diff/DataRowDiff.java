package dbfit.diff;

import dbfit.util.DataRow;
import dbfit.util.DataCell;
import dbfit.util.MatchResult;
import dbfit.util.DiffResultsSummarizer;
import static dbfit.util.DataCell.createDataCell;

public class DataRowDiff extends DiffBase<DataRow, DataRow> {
    private String[] columnNames;

    public DataRowDiff(final String[] columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public void diff(final DataRow dr1, final DataRow dr2) {
        new DataRowDiffRunner(dr1, dr2).runDiff();
    }

    private DiffResultsSummarizer createSummer(final DataRow dr1, final DataRow dr2) {
        return new DiffResultsSummarizer(
                MatchResult.create(dr1, dr2, DataRow.class), DataCell.class);
    }

    private DataCellDiff createChildDiff(final DiffResultsSummarizer summer) {
        DataCellDiff diff = new DataCellDiff();
        diff.addListeners(listeners);
        diff.addListener(summer);
        return diff;
    }

    class DataRowDiffRunner extends DiffRunner {
        private final DataRow dr1;
        private final DataRow dr2;
        private final DiffResultsSummarizer summer;

        public DataRowDiffRunner(final DataRow dr1, final DataRow dr2) {
            this.dr1 = dr1;
            this.dr2 = dr2;
            this.summer = createSummer(dr1, dr2);
        }

        @Override public MatchResult getResult() {
            return summer.getResult();
        }

        @Override
        protected void uncheckedDiff() {
            for (String column: columnNames) {
                createChildDiff(summer).diff(
                            createDataCell(dr1, column),
                            createDataCell(dr2, column));
            }
        }
    }
}
