package dbfit.environment;

import dbfit.api.AbstractDbEnvironment;
import dbfit.util.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import dbfit.annotations.DatabaseEnvironment;

@DatabaseEnvironment(name = "SybaseIQ", driver = "com.sybase.jdbc4.jdbc.SybDriver")
public class SybaseIQEnvironment extends AbstractDbEnvironment {

	public SybaseIQEnvironment(String driverClassName) {
		super(driverClassName);

	}

	@Override
	protected String getConnectionString(String dataSource) {
		return "jdbc:sybase:Tds:" + dataSource;
	}

	// for oracle, data source has to be host:port
	@Override
	protected String getConnectionString(String dataSource, String databaseName) {
		if (dataSource.indexOf(":") == -1)
			throw new UnsupportedOperationException(
					"data source should be in host:port format - " + dataSource
							+ " specified");
		return "jdbc:sybase:Tds:" + dataSource + ":" + databaseName;
	}

	private static String paramNamePattern = "@([A-Za-z0-9_]+)";
	private static Pattern paramRegex = Pattern.compile(paramNamePattern);

	public Pattern getParameterPattern() {
		return paramRegex;
	}

	protected String parseCommandText(String commandText) {
		commandText = commandText.replaceAll(paramNamePattern, "?");
		return super.parseCommandText(commandText);
	}

	@Override
	public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
			throws SQLException {

		String[] qualifiers = NameNormaliser.normaliseName(tableOrViewName)
				.split("\\.");
		String cols = " cname, coltype, length, 'IN' As is_output, 0 As is_cursor_ref ";
		String qry = "select " + cols + "  from sys.SYSCOLUMNS where 1=1 and ";
		if (qualifiers.length == 2) {
			qry += " (creator=? and tname=?) ";
		} else {
			qry += " (creator=user and tname=?)";
		}

		return readIntoParams(tableOrViewName, qry);
	}

	// List interface has sequential search, so using list instead of array to
	// map types
	private static List<String> stringTypes = Arrays.asList(new String[] {
			"VARCHAR", "CHAR" });
	private static List<String> intTypes = Arrays.asList(new String[] {
			"INTEGER", "INT" });
	private static List<String> floatTypes = Arrays
			.asList(new String[] { "REAL" });
	private static List<String> doubleTypes = Arrays.asList(new String[] {
			"FLOAT", "DOUBLE" });
	private static List<String> longTypes = Arrays
			.asList(new String[] { "BIGINT" });
	private static List<String> shortTypes = Arrays.asList(new String[] {
			"TINYINT", "SMALLINT" });
	private static List<String> decimalTypes = Arrays.asList(new String[] {
			"DECIMAL", "NUMERIC" });
	private static List<String> timestampTypes = Arrays.asList(new String[] {
			"SMALLDATETIME", "DATETIME", "TIMESTAMP", "DATE" });

	private static List<String> timeTypes = Arrays
			.asList(new String[] { "TIME" });

	private static DbParameterAccessor.Direction getParameterDirection(
			String isOutput) {
		if (isOutput.equals("OUT"))
			return DbParameterAccessor.Direction.OUTPUT;
		else
			return DbParameterAccessor.Direction.INPUT;
	}

	@Override
	public Map<String, DbParameterAccessor> getAllProcedureParameters(
			String procName) throws SQLException {

		String[] qualifiers = NameNormaliser.normaliseName(procName).split(
				"\\.");
		String cols = "case parmtype when 4 then NULL else parmname end as  parmname, parmdomain, length, rtrim(parmmode), parm_id ";
		String qry = "select " + cols
				+ "  from sys.SYSPROCPARMS where 1=1 and ";
		if (qualifiers.length == 2) {
			qry += " (upper(creator)=upper(?) and upper(procname)=upper(?)) ";
		} else {
			qry += " (creator=user and  upper(procname) = upper(?))";
		}
		qry += " order by param_id ";

		return readIntoParams(procName, qry);
	}

	private static String normaliseTypeName(String dataType) {
		dataType = dataType.toUpperCase().trim();
		int idx = dataType.indexOf(" ");
		if (idx >= 0)
			dataType = dataType.substring(0, idx);
		idx = dataType.indexOf("(");
		if (idx >= 0)
			dataType = dataType.substring(0, idx);
		return dataType;
	}

	private static int getSqlType(String dataType) {
		// todo:strip everything from first blank
		dataType = normaliseTypeName(dataType);

		if (stringTypes.contains(dataType))
			return java.sql.Types.VARCHAR;
		if (decimalTypes.contains(dataType))
			return java.sql.Types.NUMERIC;
		if (intTypes.contains(dataType))
			return java.sql.Types.INTEGER;
		if (timestampTypes.contains(dataType))
			return java.sql.Types.TIMESTAMP;
		if (timeTypes.contains(dataType))
			return java.sql.Types.TIME;
		if (floatTypes.contains(dataType))
			return java.sql.Types.FLOAT;
		if (doubleTypes.contains(dataType))
			return java.sql.Types.DOUBLE;

		if (longTypes.contains(dataType))
			return java.sql.Types.BIGINT;
		if (shortTypes.contains(dataType))
			return java.sql.Types.SMALLINT;

		throw new UnsupportedOperationException("Type " + dataType
				+ " is not supported");
	}

	public Class<?> getJavaClass(String dataType) {
		dataType = normaliseTypeName(dataType);
		if (stringTypes.contains(dataType))
			return String.class;
		if (decimalTypes.contains(dataType))
			return BigDecimal.class;
		if (intTypes.contains(dataType))
			return Integer.class;
		if (timestampTypes.contains(dataType))
			return java.sql.Timestamp.class;
		if (timeTypes.contains(dataType))
			return java.sql.Time.class;
		if (floatTypes.contains(dataType))
			return Float.class;
		if (doubleTypes.contains(dataType))
			return Double.class;
		if (longTypes.contains(dataType))
			return Long.class;
		if (shortTypes.contains(dataType))
			return Short.class;

		throw new UnsupportedOperationException("Type " + dataType
				+ " is not supported");
	}

	private Map<String, DbParameterAccessor> readIntoParams(String objname,
			String query) throws SQLException {
		String procName;

		if (objname.contains(".")) {
			String[] schemaAndName = objname.split("[\\.]", 2);
			objname = "[" + schemaAndName[0] + "].[" + schemaAndName[1] + "]";
			procName = schemaAndName[1];
		} else {
			objname = "[" + NameNormaliser.normaliseName(objname) + "]";
			procName = NameNormaliser.normaliseName(objname);
		}

		PreparedStatement dc = currentConnection.prepareStatement(query);
		dc.setString(1, NameNormaliser.normaliseName(objname));
		Log.log("executing query");
		ResultSet rs = dc.executeQuery();
		Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();
		int position = 0;
		while (rs.next()) {
			String paramName = rs.getString(1);
			if (paramName == null)
				paramName = "";
			String dataType = rs.getString(2);
			// int length = rs.getInt(3);
			String direction = rs.getString(4);
			DbParameterAccessor.Direction paramDirection;
			// if ( paramName.trim().equalsIgnoreCase(procName))
			int parameterPosition = position;
			if (paramName.trim().length() == 0) {
				paramDirection = DbParameterAccessor.Direction.RETURN_VALUE;
				parameterPosition = -1;
			} else {
				paramDirection = getParameterDirection(direction);
				++position;
			}
			DbParameterAccessor dbp = new DbParameterAccessor(paramName,
					paramDirection, getSqlType(dataType),
					getJavaClass(dataType), parameterPosition);
			allParams.put(NameNormaliser.normaliseName(paramName), dbp);
		}
		rs.close();
		return allParams;
	}

}
