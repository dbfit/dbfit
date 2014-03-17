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
        return (cell2 != null) &&
            ( (this == cell2) || getValue().equals(cell2.getValue()) );
    }

    private Object getValue() {
        return row.get(columnName);
    }
}
