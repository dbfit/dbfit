package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.ExpectedBehaviour;
import static dbfit.util.ExpectedBehaviour.*;

public class ExecuteProcedureExpectException extends ExecuteProcedure {
    private boolean excNumberDefined = false;
    private int excNumberExpected;

    public ExecuteProcedureExpectException() {
        this.environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public ExecuteProcedureExpectException(DBEnvironment dbEnvironment, String procName,
                            int expectedErrorCode) {
        this.procName = procName;
        this.environment = dbEnvironment;
        this.excNumberDefined = true;
        this.excNumberExpected = expectedErrorCode;
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
    protected int getExpectedErrorCode() {
        return excNumberExpected;
    }
}
