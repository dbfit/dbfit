package dbfit.util;

public class MatchableDataCell implements MatchableObject<MatchableDataRow> {

    private MatchableDataRow row;
    private String name;
    private String columnName;

    public MatchableDataCell(MatchableDataRow row, String columnName,
            String name) {
        this.row = row;
        this.columnName = columnName;
        this.name = name;
    }

    public MatchableDataCell(MatchableDataRow row, String columnName) {
        this(row, columnName, row.getName());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MatchableDataRow getValue() {
        return row;
    }

    @Override
    public String getStringValue() {
        return row.getValue().getStringValue(columnName);
    }
}

