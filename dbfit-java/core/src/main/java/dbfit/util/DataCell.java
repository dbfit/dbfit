package dbfit.util;

public class DataCell {
    private DataRow row;
    private String columnName;

    public DataCell(final DataRow row, final String columnName) {
        this.row = row;
        this.columnName = columnName;
    }

    /**
     *
     * @return new instance if row is non-null or return null otherwise.
     */
    public static DataCell createDataCell(final DataRow row, final String col) {
        return (row == null) ? null : new DataCell(row, col);
    }

    @Override
    public String toString() {
        return row.getStringValue(columnName);
    }

    public boolean equalTo(final DataCell cell2) {
        return (cell2 != null) && toString().equals(cell2.toString());
    }
}
