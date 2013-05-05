package dbfit.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static dbfit.util.DbParameterAccessor.Direction.*;

public class DbParameterAccessor {
    public static enum Direction {
        RETURN_VALUE,
        INPUT,
        OUTPUT,
        INPUT_OUTPUT;

        public boolean isInput() {
            return this == INPUT;
        }

        public boolean isOutOrInout() {
            switch (this) {
                case OUTPUT:
                case INPUT_OUTPUT:
                    return true;
            }

            return false;
        }

        public boolean isInOrInout() {
            switch (this) {
                case INPUT:
                case INPUT_OUTPUT:
                    return true;
            }

            return false;
        }

        public boolean isOutputOrReturnValue() {
            switch (this) {
                case RETURN_VALUE:
                case OUTPUT:
                case INPUT_OUTPUT:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isReturnValue() {
            return this == RETURN_VALUE;
        }
    }

    protected int index; //index in effective sql statement (not necessarily the same as position below)
    protected Direction direction;
    protected String name;
    protected int sqlType;
    protected Class<?> javaType;
    protected int position; //zero-based index of parameter in procedure or column in table
    protected PreparedStatement cs;

    public static Object normaliseValue(Object currVal) throws SQLException {        
        if (currVal==null) return null;
        TypeNormaliser tn=TypeNormaliserFactory.getNormaliser(currVal.getClass());
        if (tn!=null) currVal=tn.normalise(currVal);
        return currVal;
    }

    @Override
    public DbParameterAccessor clone() {
        DbParameterAccessor copy = new DbParameterAccessor(name, direction,
                sqlType, javaType, position);

        copy.cs = null;

        return copy;
    }

    @SuppressWarnings("unchecked")
    public DbParameterAccessor(String name, Direction direction, int sqlType, Class javaType, int position) {
        this.name = name;
        this.direction = direction;
        this.sqlType = sqlType;
        this.javaType = javaType;
        this.position=position;
    }

    public int getSqlType() {
        return sqlType;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    public void setDirection(Direction direction){
        this.direction = direction;
    }

    //really ugly, but a hack to support mysql, because it will not execute inserts with a callable statement
    private CallableStatement convertStatementToCallable() throws SQLException{
        if (cs instanceof CallableStatement) return (CallableStatement) cs;
        throw new SQLException("This operation requires a callable statement instead of "+cs.getClass().getName());
    }

    /*******************************************/
    public void bindTo(PreparedStatement cs, int ind) throws SQLException{
        this.cs=cs;
        this.index=ind;    
        if (direction != INPUT){
            convertStatementToCallable().registerOutParameter(ind, getSqlType());
        }
    }

    public void set(Object value) throws Exception {
        if (direction == OUTPUT|| direction == RETURN_VALUE)
            throw new UnsupportedOperationException("Trying to set value of output parameter "+name);
        cs.setObject(index, value);
    }    

    public Object get() throws IllegalAccessException, InvocationTargetException {
        try{
            if (direction.equals(INPUT))
                throw new UnsupportedOperationException("Trying to get value of input parameter "+name);            
            return normaliseValue(convertStatementToCallable().getObject(index));
        }
        catch (SQLException sqle){
            throw new InvocationTargetException(sqle);
        }
    }

    /** 
     * Zero-based column or parameter position in a query, table or stored proc
     */
    public int getPosition() {
        return position;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public boolean isReturnValueAccessor() {
        return direction.isReturnValue();
    }
}

