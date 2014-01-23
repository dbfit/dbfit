package dbfit.util;

public interface DiffListener {
    public void endRow(MatchResult<DataRow, DataRow> result);
    public void endCell(MatchResult<DataCell, DataCell> result);
}

