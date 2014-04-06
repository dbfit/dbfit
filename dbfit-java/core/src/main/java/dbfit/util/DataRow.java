package dbfit.util;

import static dbfit.util.NameNormaliser.normaliseName;
import static dbfit.util.DbParameterAccessor.normaliseValue;

import java.util.*;
import java.sql.*;

public class DataRow {
    private Map<String, Object> values = new HashMap<String, Object>();

    public Set<String> getColumnNames() {
        return values.keySet();
    }

    public DataRow(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            addValue(rsmd.getColumnLabel(i), rs.getObject(i));
        }
    }

    /**
     * Plain non-normalising c-tor
     */
    public DataRow(Map<String, Object> rowValues) {
        this.values = rowValues;
    }

    private void addValue(final String name, final Object value) throws SQLException {
        values.put(normaliseName(name), normaliseValue(value));
    }

    public String getStringValue(String columnName) {
        return String.valueOf(values.get(columnName));
    }

    public boolean matches(final Map<String, Object> keyProperties) {
        for (String key: keyProperties.keySet()) {
            if (!matches(key, keyProperties.get(key))) {
                return false;
            }
        }

        return true;
    }

    private boolean matches(final String key, final Object value) {
        String nkey = normaliseName(key);
        return values.containsKey(nkey) && equals(value, values.get(nkey));
    }

    private boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    public Object get(String key) {
        return values.get(normaliseName(key));
    }

    @Override
    public String toString() {
        return String.valueOf(values);
    }
}

