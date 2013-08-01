package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbObject;
import dbfit.api.DbStoredProcedure;
import dbfit.util.DbParameterAccessors;
import fit.Fixture;
import fit.Parse;

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

    public static class AnyExceptionRowTest extends RowTest {
        public AnyExceptionRowTest(DbParameterAccessors accessors, StatementExecution execution, Fixture parentFixture) {
            super(accessors, execution, parentFixture);
        }

        @Override
        protected void evaluateOutputs(Parse row) throws Throwable {
            if (execution.didExecutionSucceed()) {
                parentFixture.wrong(row);
                row.parts.addToBody(fit.Fixture.gray(" no exception raised"));
            } else {
                parentFixture.right(row);
            }
        }
    }

    public static class SpecificExceptionRowTest extends RowTest {
        private Integer expectedErrorCode;

        public SpecificExceptionRowTest(DbParameterAccessors accessors,
                                        StatementExecution execution,
                                        Fixture parentFixture,
                                        Integer expectedErrorCode) {
            super(accessors, execution, parentFixture);
            this.expectedErrorCode = expectedErrorCode;
        }

        @Override
        protected void evaluateOutputs(Parse row) throws Throwable {
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
    protected RowTest newRowTest(DbParameterAccessors accessors, StatementExecution execution) {
        switch (expectation) {
            case SUCCESS: return new RowTest(accessors, execution, this);
            case ANY_EXCEPTION: return new AnyExceptionRowTest(accessors, execution, this);
            case SPECIFIC_EXCEPTION: return new SpecificExceptionRowTest(accessors, execution, this, expectation.getExpectedErrorCode());
            default: throw new RuntimeException("Internal error: expectation not set");
        }
    }

    protected String getProcedureName() {
        if (procName==null) procName=args[0];
        return procName;
    }
}
