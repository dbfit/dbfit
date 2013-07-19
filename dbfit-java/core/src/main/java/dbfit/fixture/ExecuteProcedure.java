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
    private Integer expectedErrorCode;

    public ExecuteProcedure() {
        this.environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName,
                            int expectedErrorCode) {
        this.procName = procName;
        this.environment = dbEnvironment;
        this.exceptionExpected = true;
        this.expectedErrorCode = expectedErrorCode;
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName,
                            boolean exceptionExpected) {
        this.procName = procName;
        this.environment = dbEnvironment;
        this.exceptionExpected = exceptionExpected;
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
        if (expectedErrorCode == null) return ExpectedBehaviour.ANY_EXCEPTION;
        return ExpectedBehaviour.SPECIFIC_EXCEPTION;
    }

    private void executeStatementExpectingException(Parse row) throws Exception {
        try {
            getExecution().run();
            wrong(row);
        } catch (SQLException e) {
            e.printStackTrace();
            // all good, exception expected
            if (getExpectedBehaviour() == ExpectedBehaviour.ANY_EXCEPTION) {
                right(row);
            } else {
                int realErrorCode = e.getErrorCode();
                if (expectedErrorCode.equals(realErrorCode))
                    right(row);
                else {
                    wrong(row);
                    row.parts.addToBody(fit.Fixture.gray(" got error code " + realErrorCode));
                }
            }
        }
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