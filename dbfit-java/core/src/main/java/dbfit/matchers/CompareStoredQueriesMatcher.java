package dbfit.matchers;

import dbfit.util.MatchableDataTable;
import dbfit.util.MatcherListener;
import dbfit.util.RowStructure;
import dbfit.util.MatchResult;

public class CompareStoredQueriesMatcher {

    private MatchableDataTable table1;
    private MatcherListener listener;
    private RowStructure rowStructure;

    public CompareStoredQueriesMatcher(MatchableDataTable table1,
            RowStructure rowStructure, MatcherListener listener) {
        this.table1 = table1;
        this.rowStructure = rowStructure;
        this.listener = listener;
    }

    public void setListener(MatcherListener listener) {
        this.listener = listener;
    }

    public MatchResult<MatchableDataTable, MatchableDataTable> match(
            MatchableDataTable table2) {
        // TODO: implementation pending
        return null;
    }
}

