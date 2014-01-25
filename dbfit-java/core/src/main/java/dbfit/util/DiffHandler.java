package dbfit.util;

public interface DiffHandler {
    public void startTable(MatchResult<DataTable, DataTable> result);
    public void endTable(MatchResult<DataTable, DataTable> result);

    public void startRow(MatchResult<DataRow, DataRow> result);
    public void endRow(MatchResult<DataRow, DataRow> result);

    public void startCell(MatchResult<DataCell, DataCell> result);
    public void endCell(MatchResult<DataCell, DataCell> result);
}
