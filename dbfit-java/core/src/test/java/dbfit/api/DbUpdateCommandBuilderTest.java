package dbfit.api;

import dbfit.util.DbParameterAccessor;
import static dbfit.util.Direction.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import static java.util.Arrays.asList;

public class DbUpdateCommandBuilderTest {

    private final String tableName = "my_table";
    private Map<String, DbParameterAccessor> allAccessorsMap;
    private DbUpdateCommandBuilder builder = new DbUpdateCommandBuilder();
    private DBEnvironment environment = mock(DBEnvironment.class);
    private DbStatement updateCommand = mock(DbStatement.class);

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void prepare() throws Exception {
        init("a1", "a2", "a3", "b1", "b2", "b3");
    }

    private void init(String... allColumnNames) throws Exception {
        allAccessorsMap = createAccessorsMap(allColumnNames);
        when(environment.getAllColumns(tableName)).thenReturn(allAccessorsMap);
        when(environment.createDbStatement(anyString())).thenReturn(updateCommand);
        builder = new DbUpdateCommandBuilder(environment, tableName);
    }

    @Test
    public void canAddSingleUpdateParameter() {
        DbParameterAccessor acc = builder.addUpdateAccessor("a1");
        assertEquals("a1", acc.getName());
    }

    @Test
    public void canAddSingleSelectParameter() {
        DbParameterAccessor acc = builder.addSelectAccessor("b1");
        assertEquals("b1", acc.getName());
    }

    @Test
    public void canCollectSingleUpdateParameter() {
        builder.addUpdateAccessor("a1");
        assertEquals(asList("a1"), asNameList(builder.getUpdateAccessors()));
    }

    @Test
    public void canCollectSingleSelectParameter() {
        builder.addSelectAccessor("a1");
        assertEquals(asList("a1"), asNameList(builder.getSelectAccessors()));
    }

    @Test
    public void canCollectUpdateParametersFromMixedUpdate() {
        addAccessors("a1=", "a2=", "b1", "a3=", "b2");
        assertEquals(asList("a1", "a2", "a3"), asNameList(builder.getUpdateAccessors()));
    }

    @Test
    public void canCollectSelectParametersFromMixedUpdate() {
        addAccessors("a1=", "a2=", "b1", "a3=", "b2");
        assertEquals(asList("b1", "b2"), asNameList(builder.getSelectAccessors()));
    }

    @Test
    public void testBuildCommand() throws Exception {
        addAccessors("a1=", "a2=", "b1", "a3=", "b2");
        assertEquals(updateCommand, builder.build());
        verify(environment).createDbStatement(
            "update my_table set a1=?, a2=?, a3=? where b1=? and b2=?");
    }

    @Test
    public void shouldRaiseExceptionIfDbColumnListIsEmpty() throws Exception {
        expectedEx.expectMessage(
            "Cannot retrieve list of columns for " + tableName + " - check spelling and access rights");
        init();
    }

    @Test
    public void addSelectAccessorForNonExistentColumnShouldRaiseException() throws Exception {
        String paramName = "miss";
        expectedEx.expectMessage("Cannot find column " + paramName);
        builder.addSelectAccessor(paramName);
    }

    private DbParameterAccessor createAccessor(String name) {
        int sqlType = java.sql.Types.VARCHAR;
        int position = 1;
        Class<?> javaType = String.class;
        String userDefinedTypeName = "whatever";
        return new DbParameterAccessor(name, INPUT,
                sqlType, userDefinedTypeName, javaType, position, null);
    }

    private Map<String, DbParameterAccessor> createAccessorsMap(String... names) {
        Map<String, DbParameterAccessor> map = new HashMap<>();
        for (String name : names) {
            DbParameterAccessor acc = createAccessor(name);
            map.put(acc.getName(), acc);
        }
        return map;
    }

    private List<String> asNameList(List<DbParameterAccessor> accessors) {
        List<String> namesList = new ArrayList<>();
        for (DbParameterAccessor acc : accessors) {
            namesList.add(acc.getName());
        }
        return namesList;
    }

    private void addAccessors(String... names) {
        for (String name : names) {
            if (name.endsWith("=")) {
                name = name.substring(0, name.length() - 1);
                builder.addUpdateAccessor(name);
            } else {
                builder.addSelectAccessor(name);
            }
        }
    }
}
