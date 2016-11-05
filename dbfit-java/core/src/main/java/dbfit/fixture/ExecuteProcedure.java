package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbObject;
import dbfit.api.DbStoredProcedure;

import java.sql.SQLException;

public class ExecuteProcedure extends DbObjectExecutionFixture {
    protected DBEnvironment environment;
    protected String procName;
    private boolean exceptionExpected = false;
    private boolean excNumberDefined = false;
    private String excNumberExpected;

    public ExecuteProcedure() {
        this.environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName) {
        this.procName = procName;
        this.environment = dbEnvironment;
        this.exceptionExpected = true;
        this.excNumberDefined = true;
        this.excNumberExpected = expectedErrorCode;
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName,
                            boolean exceptionExpected) {
        this.procName = procName;
        this.environment = dbEnvironment;
    }

    @Override
    protected DbObject getTargetDbObject() throws SQLException {
        if (procName==null) procName=args[0];
        return new DbStoredProcedure(environment, procName);
    }

    @Override
    protected ExpectedBehaviour getExpectedBehaviour() {
        if (!exceptionExpected) return ExpectedBehaviour.NO_EXCEPTION;
        if (!excNumberDefined) return ExpectedBehaviour.ANY_EXCEPTION;
        return ExpectedBehaviour.SPECIFIC_EXCEPTION;
    }

    @Override
    protected String getExpectedErrorCode() {
        return excNumberExpected;
    }

    @Override
    protected String getActualErrorCode(SQLException e) {
System.out.println("in Execute projecure getActualErrorCode");
        return environment.getActualErrorCode(e);
    }
}
