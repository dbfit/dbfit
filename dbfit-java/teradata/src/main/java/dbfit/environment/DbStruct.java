package dbfit.environment;

import java.sql.*;

//This class is used instead of calling Connection.createStruct, which is
// only supported in JDK 6.0 or later
public class DbStruct implements Struct {

    private Object[] m_attributes;
    private String m_sqlTypeName;

    public DbStruct() {
    }

    public DbStruct(String sqlTypeName, Object[] attributes) {
        m_sqlTypeName = sqlTypeName;
        m_attributes = attributes;
    }

    // Returns attributes
    public Object[] getAttributes() throws SQLException {
        return m_attributes;
    }

    // Returns SQLTypeName
    public String getSQLTypeName() throws SQLException {
        return m_sqlTypeName;
    }

    // This method is not supported, but needs to be included
    public Object[] getAttributes(java.util.Map map) throws SQLException {
        //Unsupported Exception
        throw new SQLException("getAttributes(Map) NOT SUPPORTED");
    }
}
