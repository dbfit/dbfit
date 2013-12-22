package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbObject;
import dbfit.api.DbStoredProcedure;
import dbfit.util.actions.AnyExceptionRowAction;
import dbfit.util.actions.RowAction;
import dbfit.util.actions.SpecificExceptionRowAction;
import fit.Fixture;
import fit.Parse;

import java.sql.SQLException;

public class ExecuteProcedure extends Fixture {
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

    public static class ExecuteProcedureTable extends ExecutionTable {
        private DBEnvironment environment;
        private String procedureName;
        private Expectation expectation;

        public ExecuteProcedureTable(DBEnvironment environment, String procedureName, Expectation expectation, Fixture fixture, Parse rows) {
            super(fixture, rows);
            this.environment = environment;
            this.procedureName = procedureName;
            this.expectation = expectation;
        }

        @Override
        protected DbObject getTargetDbObject() throws SQLException {
            return new DbStoredProcedure(environment, procedureName);
        }

        @Override
        protected RowAction newRowAction(StatementExecution execution) {
            switch (expectation) {
                case SUCCESS: return new RowAction(execution);
                case ANY_EXCEPTION: return new AnyExceptionRowAction(execution);
                case SPECIFIC_EXCEPTION: return new SpecificExceptionRowAction(execution, expectation.getExpectedErrorCode());
                default: throw new RuntimeException("Internal error: expectation not set");
            }
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

    public void doRows(Parse rows) {
        try {
            new ExecuteProcedureTable(environment, getProcedureName(), expectation, this, rows).run();
        } catch (Throwable e) {
            e.printStackTrace();
            if (rows == null) throw new Error(e);
            exception(rows.parts, e);
        }
    }

    protected String getProcedureName() {
        if (procName==null) procName=args[0];
        return procName;
    }
}
