package dbfit.diff;

import dbfit.util.DataCell;
import dbfit.util.DiffListener;
import dbfit.util.MatchResult;
import static dbfit.util.MatchStatus.*;

public class DataCellDiff {
    private DataCell cell1;
    DiffListener listener;

    public DataCellDiff(DataCell cell1, DiffListener listener) {
        this.cell1 = cell1;
        this.listener = listener;
    }

    public void diff(DataCell cell2) {
        if (cell1 == null) {
            listener.endCell(MatchResult.create(cell1, cell2, SURPLUS));
        } else if (cell2 == null) {
            listener.endCell(MatchResult.create(cell1, cell2, MISSING));
        } else if (!cell1.equals(cell2)) {
            listener.endCell(MatchResult.create(cell1, cell2, WRONG));
        } else {
            listener.endCell(MatchResult.create(cell1, cell2, SUCCESS));
        }
    }
}
