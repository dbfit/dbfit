package dbfit.diff;

import java.util.List;
import java.util.ArrayList;

import dbfit.util.DataCell;
import dbfit.util.DiffListener;
import dbfit.util.MatchStatus;
import dbfit.util.MatchResult;
import static dbfit.util.MatchStatus.*;

public class DataCellDiff {
    private List<DiffListener> listeners = new ArrayList<DiffListener>();

    public DataCellDiff() {
    }

    public void addListener(final DiffListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final DiffListener listener) {
        listeners.remove(listener);
    }

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

        notifyListeners(MatchResult.create(cell1, cell2, status));
    }

    private void notifyListeners(final MatchResult<DataCell, DataCell> result) {
        for (DiffListener listener: listeners) {
            listener.endCell(result);
        }
    }
}
