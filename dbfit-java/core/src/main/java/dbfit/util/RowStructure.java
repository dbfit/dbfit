package dbfit.util;

public class RowStructure {
    private String[] columnNames;
    private boolean[] keyProperties;

    public RowStructure(String[] columnNames, boolean[] keyProperties) {
        this.columnNames = columnNames;
        this.keyProperties = keyProperties;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public boolean[] getKeyProperties() {
        return keyProperties;
    }

    public String getColumnName(int index) {
        return columnNames[index];
    }

    public boolean isKeyColumn(int index) {
        return keyProperties[index];
    }

    public int size() {
        return columnNames.length;
    }
}

