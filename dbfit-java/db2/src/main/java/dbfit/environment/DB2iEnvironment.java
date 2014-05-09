package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.NameNormaliser;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import dbfit.util.Direction;
import static dbfit.util.Direction.*;

@DatabaseEnvironment(name="DB2i", driver="com.ibm.as400.access.AS400JDBCDriver")
public class DB2iEnvironment extends AbstractDbEnvironment {

    public DB2iEnvironment(String driverClassName) {
        super(driverClassName);
    }

    protected String parseCommandText(String commandText) {
        commandText = commandText.replaceAll(paramNamePattern, "?");
        return super.parseCommandText(commandText);
    }

    private static String paramNamePattern = "[@:]([A-Za-z0-9_]+)";
    private static Pattern paramRegex = Pattern.compile(paramNamePattern);

    public Pattern getParameterPattern() {
        return paramRegex;
    }

    protected String getConnectionString(String dataSource) {
        return "jdbc:as400://" + dataSource;
    }

    protected String getConnectionString(String dataSource, String database) {
        return "jdbc:as400://" + dataSource + "/" + database;
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(tableOrViewName)
                .split("\\.");
        String qry = " select name as column_name, coltype as data_type, length, "
                + "	'P' as direction from QSYS2.SYSCOLUMNS where ";
        if (qualifiers.length == 2) {
            qry += " lower(dbname)=? and lower(tbname)=? ";
        } else {
            qry += " lower(tbname)=?";
        }
        qry += " order by name";
        return readIntoParams(qualifiers, qry);
    }

    private Map<String, DbParameterAccessor> readIntoParams(
            String[] queryParameters, String query) throws SQLException {
        try (PreparedStatement dc = currentConnection.prepareStatement(query)) {
            for (int i = 0; i < queryParameters.length; i++) {
                dc.setString(i + 1,
                        NameNormaliser.normaliseName(queryParameters[i]));
            }

            ResultSet rs = dc.executeQuery();
            Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();
            int position = 0;
            while (rs.next()) {
                String paramName = rs.getString(1);
                if (paramName == null)
                    paramName = "";
                String dataType = rs.getString(2);
                // int length=rs.getInt(3);
                String direction = rs.getString(4);
                Direction paramDirection = getParameterDirection(direction);
                DbParameterAccessor dbp = new DbParameterAccessor(paramName,
                        paramDirection, getSqlType(dataType),
                        getJavaClass(dataType),
                        paramDirection == RETURN_VALUE ? -1
                                : position++);
                allParams.put(NameNormaliser.normaliseName(paramName), dbp);
            }
            rs.close();
            return allParams;
        }
    }

    private static Direction getParameterDirection(String direction) {
        // iSeries uses QSYS2.SYSPARMS, whose values are:
        //	IN
        //	OUT
        //  INOUT
        	
        // zSeries uses SYSIBM.SYSPARMS, whose values are:
        //	P - Input
        //	O - Output
        //	B - In/Out parameter
        //	C - Result after casting (not applicable for stored procedures)  
    	
        if (("P".equals(direction)) ||
           ("IN".equals(direction)))
            return INPUT;
        if (("O".equals(direction)) ||
           ("OUT".equals(direction)))
            return OUTPUT;
        if (("B".equals(direction)) ||
           ("INOUT".equals(direction)))
            return INPUT_OUTPUT;
        if ("C".equals(direction))
            return RETURN_VALUE;
        // todo return val
        throw new UnsupportedOperationException("Direction " + direction
                + " is not supported");
    }

    private static List<String> stringTypes = Arrays.asList(new String[] {
            "VARCHAR", "CHAR", "CHARACTER", "GRAPHIC", "VARGRAPHIC", "CHARACTER VARYING" });
    private static List<String> intTypes = Arrays.asList(new String[] {
            "SMALLINT", "INT", "INTEGER" });
    private static List<String> longTypes = Arrays
            .asList(new String[] { "BIGINT" });
    private static List<String> floatTypes = Arrays
            .asList(new String[] { "FLOAT", "REAL" });
    private static List<String> doubleTypes = Arrays
            .asList(new String[] { "DOUBLE" });
    private static List<String> decimalTypes = Arrays.asList(new String[] {
            "DECIMAL", "DEC", "DECFLOAT", "NUMERIC" });
    private static List<String> dateTypes = Arrays
            .asList(new String[] { "DATE" });
    private static List<String> timestampTypes = Arrays
            .asList(new String[] { "TIMESTAMP", "TIMESTMP" });

    private static String NormaliseTypeName(String dataType) {
        dataType = dataType.toUpperCase().trim();
        return dataType;
        
    }

    private static int getSqlType(String dataType) {
       
        dataType = NormaliseTypeName(dataType);

        if (stringTypes.contains(dataType))
            return java.sql.Types.VARCHAR;
        if (decimalTypes.contains(dataType))
            return java.sql.Types.NUMERIC;
        if (intTypes.contains(dataType))
            return java.sql.Types.INTEGER;
        if (floatTypes.contains(dataType))
            return java.sql.Types.FLOAT;
        if (doubleTypes.contains(dataType))
            return java.sql.Types.DOUBLE;
        if (longTypes.contains(dataType))
            return java.sql.Types.BIGINT;
        if (timestampTypes.contains(dataType))
            return java.sql.Types.TIMESTAMP;
        if (dateTypes.contains(dataType))
            return java.sql.Types.DATE;
        throw new UnsupportedOperationException("SQL - Type " + dataType
                + " is not supported");
    }

    public Class<?> getJavaClass(String dataType) {
        dataType = NormaliseTypeName(dataType);
        if (stringTypes.contains(dataType))
            return String.class;
        if (decimalTypes.contains(dataType))
            return BigDecimal.class;
        if (intTypes.contains(dataType))
            return Integer.class;
        if (floatTypes.contains(dataType))
            return Float.class;
        if (dateTypes.contains(dataType))
            return java.sql.Date.class;
        if (doubleTypes.contains(dataType))
            return Double.class;
        if (longTypes.contains(dataType))
            return Long.class;
        if (timestampTypes.contains(dataType))
            return java.sql.Timestamp.class;
        throw new UnsupportedOperationException("Java - Type " + dataType
                + " is not supported");
    }
       
    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {
        // iSeries uses PARMNO, zSeries uses ORDINAL.
        // iSeries uses data_type, zSeries uses TYPENAME.
        // iSeries PARMMODE, zSeries uses ROWTYPE    	
        String[] qualifiers = NameNormaliser.normaliseName(procName).split(
                "\\.");
        String qry = " select parmname as column_name, data_type as data_type, precision,"
                + "	parmmode as direction, parmno from QSYS2.SYSPARMS where ";
        if (qualifiers.length == 2) {
            qry += " lower(specschema)=? and lower(specname)=? ";
        } else {
            qry += " (lower(specname)=?)";
        }
        qry += " order by parmno";
        return readIntoParams(qualifiers, qry);
    }
}
