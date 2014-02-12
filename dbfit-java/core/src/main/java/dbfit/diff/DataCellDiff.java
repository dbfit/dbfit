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
        public DataCellDiffRunner(MatchResult<DataCell, DataCell> request) {
            super(request);
        }

        @Override
        protected void uncheckedDiff() {
            if ((obj1 == null) && (obj2 == null)) {
                throw new IllegalArgumentException("Can't diff two null cells");
            } else if (obj1 == null) {
                result.setStatus(SURPLUS);
            } else if (obj2 == null) {
                result.setStatus(MISSING);
            } else if (!obj1.equalTo(obj2)) {
                result.setStatus(WRONG);
            } else {
                result.setStatus(SUCCESS);
            }
        }
    }
}
