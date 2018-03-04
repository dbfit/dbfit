package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.ExpectedBehaviour;
import static dbfit.util.ExpectedBehaviour.*;
import java.sql.SQLException;

public class ExecuteProcedureExpectException extends ExecuteProcedure {
    private boolean excNumberDefined = false;
    private String excNumberExpected;

    public ExecuteProcedureExpectException() {
        this.environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public ExecuteProcedureExpectException(DBEnvironment dbEnvironment, String procName,
                            String expectedErrorCode) {
        this.procName = procName;
        this.environment = dbEnvironment;
        this.excNumberDefined = true;
        this.excNumberExpected = expectedErrorCode;
System.out.println("ExecuteProcedureExpectException: ");
    }

    public ExecuteProcedureExpectException(DBEnvironment dbEnvironment, String procName) {
        this.procName = procName;
        this.environment = dbEnvironment;
        this.excNumberDefined = false;
    }

    @Override
    protected ExpectedBehaviour getExpectedBehaviour() {
        return excNumberDefined ? SPECIFIC_EXCEPTION : ANY_EXCEPTION;
    }

    @Override
    protected String getExpectedErrorCode() {
        return excNumberExpected;
    }

    @Override
    protected String getActualErrorCode(SQLException e) {
System.out.println("ExecuteProcedureExpectException: getActualErrorCode");
        return environment.getActualErrorCode(e);
    }
}
