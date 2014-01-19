package dbfit.util;

public class MatchableDataRow implements MatchableObject<DataRow> {

    private DataRow row;
    private String name;

    public MatchableDataRow(DataRow row, String name) {
        this.row = row;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataRow getValue() {
        return row;
    }

    @Override
    public String getStringValue() {
        return row.toString();
    }
}

