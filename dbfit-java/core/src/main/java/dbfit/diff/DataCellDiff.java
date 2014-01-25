package dbfit.diff;

import dbfit.util.DataCell;
import dbfit.util.MatchStatus;
import dbfit.util.MatchResult;
import static dbfit.util.MatchStatus.*;

public class DataCellDiff extends DiffBase {

    public void diff(final DataCell cell1, final DataCell cell2) {
        MatchStatus status;
        if (cell1 == null) {
            status = SURPLUS;
        } else if (cell2 == null) {
            status = MISSING;
        } else if (!cell1.equalTo(cell2)) {
            status = WRONG;
        } else {
            status = SUCCESS;
        }

        notifyListeners(MatchResult.create(cell1, cell2, status, DataCell.class));
    }
}
