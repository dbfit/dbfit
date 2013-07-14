package dbfit.api;

import dbfit.util.ParameterOrColumn;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.SQLException;
import java.util.HashMap;

import static dbfit.util.Direction.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DbTableTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void exceptionWhenAccessingNonexistentColumn() throws SQLException {
        DBEnvironment env = mock(DBEnvironment.class);

        when(env.getAllColumns("mytable")).thenReturn(columns("some_column"));
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("No such database column: 'nonexistent_column'");

        new DbTable(env, "mytable").getDbParameterAccessor("nonexistent_column", INPUT);
    }

    private HashMap<String, ParameterOrColumn> columns(String... columnNames) {
        HashMap<String, ParameterOrColumn> columns = new HashMap<String, ParameterOrColumn>();
        for (String columnName : columnNames) {
            columns.put(columnName, null);
        }
        return columns;
    }
}
