package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbObject;
import dbfit.api.DbStoredProcedure;
import dbfit.util.ExpectedBehaviour;
import fit.Parse;

import java.sql.SQLException;

public class ExecuteProcedure extends DbObjectExecutionFixture {
    private DBEnvironment environment;
    private String procName;
    private boolean exceptionExpected = false;
    private boolean excNumberDefined = false;
    private int excNumberExpected;

    public ExecuteProcedure() {
        this.environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName,
                            int expectedErrorCode) {
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
        this.exceptionExpected = exceptionExpected;
        this.excNumberDefined = false;
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName) {
        this(dbEnvironment, procName, false);
    }

    @Override
    protected DbObject getTargetDbObject() throws SQLException {
        if (procName==null) procName=args[0];
        return new DbStoredProcedure(environment, procName);
    }

    protected ExpectedBehaviour getExpectedBehaviour() {
        if (!exceptionExpected) return ExpectedBehaviour.NO_EXCEPTION;
        if (!excNumberDefined) return ExpectedBehaviour.ANY_EXCEPTION;
        return ExpectedBehaviour.SPECIFIC_EXCEPTION;
    }

    protected int getExpectedErrorCode() {
        return excNumberExpected;
    }

    private void executeStatementExpectingException(Parse row) throws Exception {
        try {
            execution.createSavepoint();
            execution.run();
            wrong(row);
        } catch (SQLException e) {
            e.printStackTrace();
            // all good, exception expected
            if (getExpectedBehaviour() == ExpectedBehaviour.ANY_EXCEPTION) {
                right(row);
            } else {
                int realError = ((DbStoredProcedure) dbObject).getExceptionCode(e);
                if (realError == getExpectedErrorCode())
                    right(row);
                else {
                    wrong(row);
                    row.parts.addToBody(fit.Fixture.gray(" got error code " + realError));
                }
            }
        }
        execution.restoreSavepoint();
    }

    @Override
    protected void executeStatementAndEvaluateOutputs(Parse row) throws Throwable {
        if (getExpectedBehaviour() == ExpectedBehaviour.NO_EXCEPTION) {
            super.executeStatementAndEvaluateOutputs(row);
        } else {
            executeStatementExpectingException(row);
        };
    }
}
