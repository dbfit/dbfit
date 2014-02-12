package dbfit.util;

public interface DiffHandler {

    public void endTable(MatchResult<DataTable, DataTable> result);

    public void endRow(MatchResult<DataRow, DataRow> result);

    public void endCell(MatchResult<DataCell, DataCell> result);
}
