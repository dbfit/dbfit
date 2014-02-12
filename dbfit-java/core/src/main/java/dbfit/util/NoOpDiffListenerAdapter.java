package dbfit.util;

public class NoOpDiffListenerAdapter extends DiffListenerAdapter
                                     implements DiffHandler {

    public NoOpDiffListenerAdapter() {
        setDiffHandler(this);
    }

    @Override
    public void endTable(MatchResult<DataTable, DataTable> result) {}

    @Override
    public void endRow(MatchResult<DataRow, DataRow> result) {}

    @Override
    public void endCell(MatchResult<DataCell, DataCell> result) {}
}
