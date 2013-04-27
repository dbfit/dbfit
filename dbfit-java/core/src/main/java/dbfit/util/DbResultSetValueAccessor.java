package dbfit.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbResultSetValueAccessor extends DbParameterAccessor {
	
	public DbResultSetValueAccessor (DbParameterAccessor c){
		super(c.getName(),Direction.OUTPUT,c.getSqlType(),c.javaType,c.getPosition());
	}
	private PreparedStatement statement;
	public void bindTo(PreparedStatement cs, int ind) throws SQLException{
		this.statement=cs;
	}
	public void set(Object value) throws Exception {
			throw new UnsupportedOperationException("Trying to set value of output parameter "+getName());
	}	
	public Object get() throws IllegalAccessException, InvocationTargetException {
		try{
		ResultSet rs=statement.getResultSet();
		if (rs.next()) //todo: first try to find by name 
			return rs.getObject(1);
		}
		catch (SQLException sqle){
			throw new InvocationTargetException(sqle);
		}
		throw new IllegalAccessException("statement has not generated any keys");
	}
}
