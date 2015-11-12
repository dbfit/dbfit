package dbfit.environment;

import org.junit.*;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Map;

import dbfit.api.DBEnvironment;

import dbfit.util.TypeSpecifier;

public class TypeSpecifierTest {

    private class DummyTypeSpecifier implements TypeSpecifier {
        public Object specify(Object o) throws SQLException {
            return new String("abcdef");
        }
    }

    @Test
    public void castTypeTest() {
        DBEnvironment env = dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Dummy");
        env.setTypeSpecifier(java.util.Date.class, new DummyTypeSpecifier());
        Map<Class<?>, TypeSpecifier> tsm = env.getTypeSpecifierMap();
        TypeSpecifier ts = tsm.get(java.util.Date.class);
        String s = null;
        try {
            s = (String) ts.specify(new java.util.Date());
        } catch (SQLException e) {
            fail();
        }
        assertEquals(s, "abcdef");
    }
}
