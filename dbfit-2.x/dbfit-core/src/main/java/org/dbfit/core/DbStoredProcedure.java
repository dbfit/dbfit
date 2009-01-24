package org.dbfit.core;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import dbfit.util.DbParameterAccessor;
import dbfit.util.NameNormaliser;

public class DbStoredProcedure implements DbObject {
	private DBEnvironment environment;
	private String storedProcName;
	private Map<String, DbParameterAccessor> allParams;
	
	public DbStoredProcedure(DBEnvironment environment, String storedProcName) throws Exception{
		this.environment = environment;
		this.storedProcName = storedProcName;
	}
	
	private class PositionComparator implements Comparator<DbParameterAccessor> {
		public int compare(DbParameterAccessor o1, DbParameterAccessor o2) {
			return (int) Math.signum(o1.getPosition() - o2.getPosition());
		}
	}

	private List<String> getSortedAccessorNames(DbParameterAccessor[] accessors) {
		DbParameterAccessor[] newacc = new DbParameterAccessor[accessors.length];
		System.arraycopy(accessors, 0, newacc, 0, accessors.length);
		Arrays.sort(newacc, new PositionComparator());
		List<String> nameList = new ArrayList<String>();
		String lastName = null;
		for (DbParameterAccessor p : newacc) {
			if (lastName != p.getName()) {
				lastName = p.getName();
				nameList.add(p.getName());
			}
		}
		return nameList;
	}

	private boolean containsReturnValue(DbParameterAccessor[] accessors) {
		for (DbParameterAccessor ac : accessors) {
			if (ac.getDirection() == DbParameterAccessor.RETURN_VALUE)
				return true;
		}
		return false;
	}

	public CallableStatement buildCommand(String procName,
			DbParameterAccessor[] accessors) throws SQLException {
		List<String> accessorNames = getSortedAccessorNames(accessors);
		boolean isFunction = containsReturnValue(accessors);

		StringBuilder ins = new StringBuilder("{ ");
		if (isFunction) {
			ins.append("? =");
		}
		ins.append("call ").append(procName);
		String comma = "(";
		boolean hasArguments = false;
		for (int i = (isFunction ? 1 : 0); i < accessorNames.size(); i++) {
			ins.append(comma);
			ins.append("?");
			comma = ",";
			hasArguments = true;
		}
		if (hasArguments)
			ins.append(")");
		ins.append("}");

		CallableStatement cs = environment.getConnection().prepareCall(
				ins.toString());
		for (DbParameterAccessor ac : accessors) {
			int realindex = accessorNames.indexOf(ac.getName());
			ac.bindTo(cs, realindex + 1); // jdbc params are 1-based
			if (ac.getDirection() == DbParameterAccessor.RETURN_VALUE) {
				ac.bindTo(cs, Math.abs(ac.getPosition()));
			}
		}
		return cs;
	}

	public PreparedStatement buildPreparedStatement(
			DbParameterAccessor[] accessors) throws SQLException {
		return buildCommand(storedProcName, accessors);
	}
	public DbParameterAccessor getDbParameterAccessor(String name,
			int expectedDirection) throws SQLException{
	
		if (allParams==null){
			allParams = environment.getAllProcedureParameters(storedProcName);
			if (allParams.isEmpty()) {
				throw new SQLException("Cannot retrieve list of parameters for "
						+ storedProcName + " - check spelling and access rights");
			}
		}
		String paramName = NameNormaliser.normaliseName(name);
		DbParameterAccessor accessor = allParams.get(paramName);
		if (accessor == null)
			throw new SQLException("Cannot find parameter \"" + paramName + "\"");
		if (accessor.getDirection() == DbParameterAccessor.INPUT_OUTPUT) {
			// clone, separate into input and output
			accessor = new DbParameterAccessor(accessor);
			accessor.setDirection(expectedDirection);
		}
		// sql server quirk. if output parameter is used in an input column,
		// then the param should be cloned and remapped to IN/OUT
		if (expectedDirection!=DbParameterAccessor.OUTPUT && 
				accessor.getDirection() == DbParameterAccessor.OUTPUT) {
			accessor = new DbParameterAccessor(accessor);
			accessor.setDirection(DbParameterAccessor.INPUT);
		}
		return accessor;
	}
}
