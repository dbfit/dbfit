package dbfit.util;

public class NoOpDiffListenerAdapter extends DiffListenerAdapter
                                     implements DiffHandler {

    public NoOpDiffListenerAdapter() {
        init(this);
    }

    @Override
    public void startTable(MatchResult<DataTable, DataTable> result) {}

    @Override
    public void endTable(MatchResult<DataTable, DataTable> result) {}

    @Override
    public void startRow(MatchResult<DataRow, DataRow> result) {}

    @Override
    public void endRow(MatchResult<DataRow, DataRow> result) {}

    @Override
    public void startCell(MatchResult<DataCell, DataCell> result) {}

    @Override
    public void endCell(MatchResult<DataCell, DataCell> result) {}
}
