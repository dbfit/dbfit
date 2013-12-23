package dbfit.util;

import static dbfit.util.NameNormaliser.normaliseName;

import java.util.*;
import java.sql.*;
import org.apache.commons.lang3.ObjectUtils;

public class DataRow {
    private Map<String, Object> values = new HashMap<String, Object>();
    private boolean processed = false;

    public Set<String> getColumnNames() {
        return values.keySet();
    }

    public DataRow(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
        for(int i = 1; i <= rsmd.getColumnCount(); i++) {
            Object val = rs.getObject(i);

            // Log.log("loading data from "+rsmd.getColumnName(i) +" = "+
            // val == null?"NULL":(val.getClass() + " " + val));
            values.put(
                    normaliseName(rsmd.getColumnLabel(i)),
                    DbParameterAccessor.normaliseValue(val));
        }
    }

    public String getStringValue(String columnName) {
        return ObjectUtils.toString(values.get(columnName), "null");
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
        return ObjectUtils.equals(a, b);
    }

    public Object get(String key) {
        return values.get(normaliseName(key));
    }

    public void markProcessed() {
        processed = true;
    }

    public boolean isProcessed() {
        return processed;
    }
}

