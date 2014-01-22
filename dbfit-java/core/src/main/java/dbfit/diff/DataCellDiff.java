package dbfit.diff;

import dbfit.util.DataCell;
import dbfit.util.MatcherListener;
import dbfit.util.MatchResult;

public class DataCellDiff {
    private DataCell cell1;
    MatcherListener listener;

    public DataCellDiff(DataCell cell1, MatcherListener listener) {
        this.cell1 = cell1;
        this.listener = listener;
    }

    public void diff(DataCell cell2) {
    }
}
