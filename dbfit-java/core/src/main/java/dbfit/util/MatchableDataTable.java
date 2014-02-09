package dbfit.util;

import java.util.*;

/**
 * Support for data row matching and tracking processed/unprocessed rows
 * of DataTable.
 */
public class MatchableDataTable {

    private final DataTable dt;
    private final LinkedList<DataRow> unprocessedRows;

    public MatchableDataTable(final DataTable dt) {
        this.dt = dt;
        unprocessedRows = new LinkedList<DataRow>(dt.getRows());
    }

    public DataRow findMatching(final Map<String, Object> keyProperties) throws NoMatchingRowFoundException {
        return verified(findMatchingNothrow(keyProperties));
    }

    public DataRow findMatchingNothrow(final Map<String, Object> keyProperties) {
        for (DataRow dr: getUnprocessedRows()) {
            if (dr.matches(keyProperties)) {
                return dr;
            }
        }

        return null;
    }

    public DataRow findFirstUnprocessedRow() throws NoMatchingRowFoundException {
        try {
            return unprocessedRows.getFirst();
        } catch (NoSuchElementException e) {
            throw new NoMatchingRowFoundException();
        }
    }

    public List<DataRow> getUnprocessedRows() {
        return unprocessedRows;
    }

    public void markProcessed(final DataRow dr) {
        unprocessedRows.remove(dr);
    }

    public List<DataColumn> getColumns() {
        return dt.getColumns();
    }

    private DataRow verified(final DataRow row) throws NoMatchingRowFoundException {
        if (row == null) {
            throw new NoMatchingRowFoundException();
        }

        return row;
    }
}

