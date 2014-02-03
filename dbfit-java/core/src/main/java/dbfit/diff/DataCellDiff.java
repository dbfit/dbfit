package dbfit.diff;

import dbfit.util.DataCell;
import dbfit.util.MatchResult;
import static dbfit.util.MatchStatus.*;

public class DataCellDiff extends DiffBase {

    public void diff(final DataCell cell1, final DataCell cell2) {
        MatchResult result = MatchResult.create(cell1, cell2, DataCell.class);

        try {
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
        } catch(Exception e) {
            result.setException(e);
        }

        notifyListeners(result);
    }
}
