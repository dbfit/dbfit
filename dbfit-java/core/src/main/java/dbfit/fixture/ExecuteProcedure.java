package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbObject;
import dbfit.api.DbStoredProcedure;
import fit.Parse;

import java.sql.SQLException;

public class ExecuteProcedure extends DbObjectExecutionFixture {
    public interface ExecutionExpectation {
        void evaluateOutputs(StatementExecution execution, Parse row) throws Throwable;
    }

    public static class SuccessfulExecutionExpectation implements ExecutionExpectation {
        private ExecuteProcedure fixture;

        public SuccessfulExecutionExpectation(ExecuteProcedure fixture) {
            this.fixture = fixture;
        }

        public void evaluateOutputs(StatementExecution execution, Parse row) throws Throwable {
            fixture.evaluateOutputsUsingSuperclass(row);
        }
    }

    public static class AnyExceptionExpectation implements ExecutionExpectation {
        private DbObjectExecutionFixture parentFixture;

        public AnyExceptionExpectation(DbObjectExecutionFixture parentFixture) {
            this.parentFixture = parentFixture;
        }

        public void evaluateOutputs(StatementExecution execution, Parse row) throws Throwable {
            if (execution.didExecutionSucceed()) {
                parentFixture.wrong(row);
                row.parts.addToBody(fit.Fixture.gray(" no exception raised"));
            } else {
                parentFixture.right(row);
            }
        }
    }

    public static class SpecificExceptionExpectation implements ExecutionExpectation {
        private DbObjectExecutionFixture parentFixture;
        private Integer expectedErrorCode;

        public SpecificExceptionExpectation(DbObjectExecutionFixture parentFixture,
                                            Integer expectedErrorCode) {
            this.parentFixture = parentFixture;
            this.expectedErrorCode = expectedErrorCode;
        }

        public void evaluateOutputs(StatementExecution execution, Parse row) throws Throwable {
            if (execution.didExecutionSucceed()) {
                parentFixture.wrong(row);
                row.parts.addToBody(fit.Fixture.gray(" no exception raised"));
            } else if (expectedErrorCode.equals(getActualErrorCodeFrom(execution))) {
                parentFixture.right(row);
            } else {
                parentFixture.wrong(row);
                row.parts.addToBody(fit.Fixture.gray(" got error code " + getActualErrorCodeFrom(execution)));
            }
        }

        private int getActualErrorCodeFrom(StatementExecution execution) {
            SQLException e = execution.getEncounteredException();
            return e.getErrorCode();
        }
    }

    private DBEnvironment environment;
    private String procName;
    private ExecutionExpectation expectation;

    public ExecuteProcedure() {
        this.environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName, int expectedErrorCode) {
        this(dbEnvironment, procName);

        expectation = new SpecificExceptionExpectation(this, expectedErrorCode);
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName, boolean exceptionExpected) {
        this(dbEnvironment, procName);

        if (exceptionExpected)
            expectation = new AnyExceptionExpectation(this);
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName) {
        this.procName = procName;
        this.environment = dbEnvironment;

        expectation = new SuccessfulExecutionExpectation(this);
    }

    @Override
    protected DbObject getTargetDbObject() throws SQLException {
        return new DbStoredProcedure(environment, getProcedureName());
    }

    protected void evaluateOutputsUsingSuperclass(Parse row) throws Throwable {
        super.evaluateOutputs(row);
    }

    protected String getProcedureName() {
        if (procName==null) procName=args[0];
        return procName;
    }

    @Override
    protected void evaluateOutputs(Parse row) throws Throwable {
        expectation.evaluateOutputs(getExecution(), row);
    }
}
