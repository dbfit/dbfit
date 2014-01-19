package dbfit.util;

import java.util.*;

/**
 * Support for data row matching and tracking processed/unprocessed rows
 * of DataTable.
 */
public class MatchableDataTable implements MatchableObject<DataTable> {

    private final DataTable dt;
    private final LinkedList<DataRow> unprocessedRows;
    private String name;

    public MatchableDataTable(final DataTable dt, String name) {
        this.dt = dt;
        this.name = name;
        unprocessedRows = new LinkedList<DataRow>(dt.getRows());
    }

    public MatchableDataTable(final DataTable dt) {
        this(dt, "");
    }

    public DataRow findMatching(final Map<String,Object> keyProperties) throws NoMatchingRowFoundException {
        for (DataRow dr: getUnprocessedRows()) {
            if (dr.matches(keyProperties)) {
                return dr;
            }
        }

        throw new NoMatchingRowFoundException();
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

    public void processDataRows(DataRowProcessor processor) {
        Iterator<DataRow> unprocIter = unprocessedRows.iterator();

        while (unprocIter.hasNext()) {
            processor.process(unprocIter.next());
            unprocIter.remove();
        }
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataTable getValue() {
        return dt;
    }

    @Override
    public String getStringValue() {
        return dt.toString();
    }
}

