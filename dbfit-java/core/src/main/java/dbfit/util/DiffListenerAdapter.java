package dbfit.util;

public class DiffListenerAdapter implements DiffListener {

    private DiffHandler delegate;

    public static DiffListenerAdapter from(final DiffHandler handler) {
        return new DiffListenerAdapter(handler);
    }

    protected DiffListenerAdapter() {}

    public DiffListenerAdapter(final DiffHandler delegate) {
        setDiffHandler(delegate);
    }

    protected void setDiffHandler(final DiffHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onEvent(final MatchResult res) {
        Class type = res.getType();

        if (type.equals(DataCell.class)) {
            delegate.endCell((MatchResult<DataCell, DataCell>) res);
        } else if (type.equals(DataRow.class)) {
            delegate.endRow((MatchResult<DataRow, DataRow>) res);
        } else if (type.equals(DataTable.class)) {
            delegate.endTable((MatchResult<DataTable, DataTable>) res);
        } else {
            throw new IllegalArgumentException("Unknown type: " + res.getType());
        }
    }

}
