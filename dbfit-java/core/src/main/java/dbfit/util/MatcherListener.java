package dbfit.util;

public interface MatcherListener {
    public void endRow(MatchResult<MatchableDataRow, MatchableDataRow> result);
    public void endCell(MatchResult<MatchableDataCell, MatchableDataCell> result);
}

