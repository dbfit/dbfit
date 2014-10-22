package dbfit.util;

import dbfit.fixture.StatementExecution;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import static dbfit.util.Direction.*;

public class DbParameterAccessor {

    private int index; //index in effective sql statement (not necessarily the same as position below)
    private Direction direction;
    private String name;
    private int sqlType;
    private String userDefinedTypeName;
    private Class<?> javaType;
    private int position; //zero-based index of parameter in procedure or column in table
    protected StatementExecution cs;

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
        this(name, direction, sqlType, null, javaType, position);
    }

    public DbParameterAccessor(String name, Direction direction, int sqlType, String userDefinedTypeName, Class javaType, int position) {
        this.name = name;
        this.direction = direction;
        this.sqlType = sqlType;
        this.userDefinedTypeName = userDefinedTypeName;
        this.javaType = javaType;
        this.position=position;
    }

    protected int getSqlType() {
        return sqlType;
    }

    public String getUserDefinedTypeName() {
        return userDefinedTypeName;
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

    /*******************************************/
    public void bindTo(StatementExecution cs, int ind) throws SQLException{
        this.cs=cs;
        this.index=ind;    
        if (direction != INPUT){
            cs.registerOutParameter(ind, getSqlType());
        }
    }

    public void set(Object value) throws Exception {
        if (direction == OUTPUT|| direction == RETURN_VALUE)
            throw new UnsupportedOperationException("Trying to set value of output parameter "+name);
        cs.setObject(index, value, sqlType, userDefinedTypeName);
    }    

    public Object get() throws IllegalAccessException, InvocationTargetException {
        try{
            if (direction.equals(INPUT))
                throw new UnsupportedOperationException("Trying to get value of input parameter "+name);            
            return normaliseValue(cs.getObject(index));
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

    public boolean hasDirection(Direction expectedDirection) {
        return getDirection() == expectedDirection;
    }

    public boolean doesNotHaveDirection(Direction expectedDirection) {
        return !hasDirection(expectedDirection);
    }
}

