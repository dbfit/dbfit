package dbfit.diff;

import dbfit.util.DataCell;
import dbfit.util.MatchResult;
import static dbfit.util.MatchStatus.*;

public class DataCellDiff extends DiffBase {

    public void diff(final DataCell cell1, final DataCell cell2) {
        new DataCellDiffRunner(cell1, cell2).runDiff();
    }

    class DataCellDiffRunner extends DiffRunner {
        private final DataCell cell1;
        private final DataCell cell2;
        private final MatchResult result;

        public DataCellDiffRunner(final DataCell cell1, final DataCell cell2) {
            this.cell1 = cell1;
            this.cell2 = cell2;
            this.result = MatchResult.create(cell1, cell2, DataCell.class);
        }

        @Override public MatchResult getResult() {
            return result;
        }

        @Override
        protected void uncheckedDiff() {
            if ( (cell1 == null) && (cell2 == null) ) {
                throw new IllegalArgumentException("Can't diff two null cells");
            } else if (cell1 == null) {
                result.setStatus(SURPLUS);
            } else if (cell2 == null) {
                result.setStatus(MISSING);
            } else if (!cell1.equalTo(cell2)) {
                result.setStatus(WRONG);
            } else {
                result.setStatus(SUCCESS);
            }
        }
    }
}
