package dbfit.util;

public interface DiffListener {
    public void endRow(MatchResult<MatchableDataRow, MatchableDataRow> result);
    public void endCell(MatchResult<DataCell, DataCell> result);
}

