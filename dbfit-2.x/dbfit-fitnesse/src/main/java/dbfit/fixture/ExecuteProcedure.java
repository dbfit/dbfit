package dbfit.fixture;

import java.sql.SQLException;

import org.dbfit.core.DBEnvironment;
import org.dbfit.core.DbEnvironmentFactory;
import org.dbfit.core.DbObject;
import org.dbfit.core.DbStoredProcedure;

import dbfit.util.ExpectedBehaviour;

public class ExecuteProcedure extends DbObjectExecutionFixture{
	private DBEnvironment environment;
	private String procName;
	private boolean exceptionExpected=false;
    private boolean excNumberDefined=false;
    private int excNumberExpected;
	public ExecuteProcedure()
    {
        this.environment = DbEnvironmentFactory.getDefaultEnvironment();
    }
	public ExecuteProcedure(DBEnvironment dbEnvironment, String procName, 
			int expectedErrorCode) {
		this.procName= procName;
		this.environment = dbEnvironment;		
		this.exceptionExpected=true;
		this.excNumberDefined=true;
		this.excNumberExpected=expectedErrorCode;
	}
	public ExecuteProcedure(DBEnvironment dbEnvironment, String procName, 
			boolean exceptionExpected) {
		this.procName= procName;
		this.environment = dbEnvironment;		
		this.exceptionExpected=exceptionExpected;
		this.excNumberDefined=false;
	}
	public ExecuteProcedure(DBEnvironment dbEnvironment, String procName) {
		this(dbEnvironment,procName,false);		
	}
	@Override
	protected DbObject getTargetDbObject() throws SQLException {
		return new DbStoredProcedure(environment,procName);
	}
	@Override
	protected ExpectedBehaviour getExpectedBehaviour() {
		if (!exceptionExpected) return ExpectedBehaviour.NO_EXCEPTION;
		if (!excNumberDefined) return ExpectedBehaviour.ANY_EXCEPTION;
		return ExpectedBehaviour.SPECIFIC_EXCEPTION;
	}
	@Override
	protected int getExpectedErrorCode() {
		return excNumberExpected;
	}
}
