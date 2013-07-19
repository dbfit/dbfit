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
    private int actualErrorCode;

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

    @Override
    protected void executeStatement(Parse row) throws SQLException {
        if (notExpectingException()) {
            super.executeStatement(row);
        } else {
            executeStatementExpectingException(row);
        }
    }

    private void executeStatementExpectingException(Parse row) {
        try {
            getExecution().run();
            wrong(row);
        } catch (SQLException e) {
            actualErrorCode = e.getErrorCode();
            e.printStackTrace();
        }
    }

    @Override
    protected void evaluateOutputs(Parse row) throws Throwable {
        if (notExpectingException()) {
            super.evaluateOutputs(row);
        } else if (isExpectingAnyException()) {
            right(row);
        } else {
            if (expectedErrorCode.equals(actualErrorCode))
                right(row);
            else {
                wrong(row);
                row.parts.addToBody(fit.Fixture.gray(" got error code " + actualErrorCode));
            }
        }
    }

    private boolean notExpectingException() {
        return getExpectedBehaviour() == ExpectedBehaviour.NO_EXCEPTION;
    }

    private boolean isExpectingAnyException() {
        return getExpectedBehaviour() == ExpectedBehaviour.ANY_EXCEPTION;
    }
}
