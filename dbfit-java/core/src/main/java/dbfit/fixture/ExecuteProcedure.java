package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbObject;
import dbfit.api.DbStoredProcedure;

import java.sql.SQLException;

public class ExecuteProcedure extends DbObjectExecutionFixture {
    public enum Expectation {
        SUCCESS,
        ANY_EXCEPTION,
        SPECIFIC_EXCEPTION;

        Integer expectedErrorCode;

        public void setExpectedErrorCode(Integer expectedErrorCode) {
            this.expectedErrorCode = expectedErrorCode;
        }

        public Integer getExpectedErrorCode() {
            return expectedErrorCode;
        }
    }

    public static class AnyExceptionRowAction extends RowAction {
        public AnyExceptionRowAction(StatementExecution execution) {
            super(execution);
        }

        @Override
        protected void evaluateOutputs(Row row) throws Throwable {
            if (execution.didExecutionSucceed()) {
                row.getTestResultHandler().fail("no exception raised");
            } else {
                row.getTestResultHandler().pass();
            }
        }
    }

    public static class SpecificExceptionRowAction extends RowAction {
        private Integer expectedErrorCode;

        public SpecificExceptionRowAction(StatementExecution execution,
                                          Integer expectedErrorCode) {
            super(execution);
            this.expectedErrorCode = expectedErrorCode;
        }

        @Override
        protected void evaluateOutputs(Row row) throws Throwable {
            if (execution.didExecutionSucceed()) {
                row.getTestResultHandler().fail("no exception raised");
            } else if (expectedErrorCode.equals(getActualErrorCodeFrom(execution))) {
                row.getTestResultHandler().pass();
            } else {
                row.getTestResultHandler().fail(" got error code " + getActualErrorCodeFrom(execution));
            }
        }

        private int getActualErrorCodeFrom(StatementExecution execution) {
            SQLException e = execution.getEncounteredException();
            return e.getErrorCode();
        }
    }

    private DBEnvironment environment;
    private String procName;
    private Expectation expectation;

    public ExecuteProcedure() {
        this.environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName, int expectedErrorCode) {
        this(dbEnvironment, procName);

        expectation = Expectation.SPECIFIC_EXCEPTION;
        expectation.setExpectedErrorCode(expectedErrorCode);
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName, boolean exceptionExpected) {
        this(dbEnvironment, procName);

        if (exceptionExpected)
            expectation = Expectation.ANY_EXCEPTION;
    }

    public ExecuteProcedure(DBEnvironment dbEnvironment, String procName) {
        this.procName = procName;
        this.environment = dbEnvironment;

        expectation = Expectation.SUCCESS;
    }

    @Override
    protected DbObject getTargetDbObject() throws SQLException {
        return new DbStoredProcedure(environment, getProcedureName());
    }

    @Override
    protected RowAction newRowTest(StatementExecution execution) {
        switch (expectation) {
            case SUCCESS: return new RowAction(execution);
            case ANY_EXCEPTION: return new AnyExceptionRowAction(execution);
            case SPECIFIC_EXCEPTION: return new SpecificExceptionRowAction(execution, expectation.getExpectedErrorCode());
            default: throw new RuntimeException("Internal error: expectation not set");
        }
    }

    protected String getProcedureName() {
        if (procName==null) procName=args[0];
        return procName;
    }
}
