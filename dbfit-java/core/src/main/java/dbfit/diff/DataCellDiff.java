package dbfit.diff;

import dbfit.util.DataCell;
import dbfit.util.MatchResult;
import static dbfit.util.MatchStatus.*;

public class DataCellDiff extends DiffBase<DataCell, DataCell> {

    @Override
    protected Class getType() {
        return DataCell.class;
    }

    @Override
    protected DiffRunner getDiffRunner(MatchResult<DataCell, DataCell> request) {
        return new DataCellDiffRunner(request);
    }

    class DataCellDiffRunner extends DiffRunner {
        private final DataCell cell1;
        private final DataCell cell2;
        private final MatchResult result;

        public DataCellDiffRunner(MatchResult<DataCell, DataCell> request) {
            this.result = request;
            this.cell1 = request.getObject1();
            this.cell2 = request.getObject2();
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
