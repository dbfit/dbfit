package dbfit.util;

import dbfit.fixture.StatementExecution;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class DbAutoGeneratedKeyAccessor extends DbParameterAccessor {

    public DbAutoGeneratedKeyAccessor(DbParameterAccessor c) {
        super(c.getName(), Direction.RETURN_VALUE, c.getSqlType(), c.getJavaType(), c.getPosition(), c.getDbfitToJDBCTransformerFactory());
    }

    @Override
    public void bindTo(StatementExecution cs, int ind) throws SQLException {
        this.cs = cs;
    }

    @Override
    public void set(Object value) throws Exception {
        throw new UnsupportedOperationException("Trying to set value of output parameter " + getName());
    }

    @Override
    public Object get() throws IllegalAccessException, InvocationTargetException {
        try {
            return cs.getGeneratedKey(getJavaType());
        } catch (SQLException e) {
            throw new InvocationTargetException(e);
        }
    }
}
