package dbfit.fixture;

import java.sql.SQLException;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbObject;
import dbfit.api.DbStatement;
import dbfit.util.ExpectedBehaviour;
import dbfit.util.FitNesseTestHost;

public class Execute extends DbObjectExecutionFixture {
    private String statementText;
    private DBEnvironment dbEnvironment;
    private boolean exceptionExpected = false;
    private boolean excNumberDefined = false;
    private int excNumberExpected;
    
    public Execute() {
        dbEnvironment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public Execute(DBEnvironment env, String statement, int expectedErrorCode) {
        this.statementText = statement;
        this.dbEnvironment = env;
        this.exceptionExpected = true;
        this.excNumberDefined = true;
        this.excNumberExpected = expectedErrorCode;
    }
    
    public Execute(DBEnvironment env, String statement, boolean exceptionExpected) {
        this.statementText = statement;
        this.dbEnvironment = env;
        this.exceptionExpected = exceptionExpected;
        this.excNumberDefined = false;
    }
    
    public Execute(DBEnvironment env, String statement) {
        this(env, statement, false);
    }
    
    @Override
    protected DbObject getTargetDbObject() throws SQLException {
        if (statementText == null) {
            statementText = args[0];
        }
        return new DbStatement(dbEnvironment, statementText, FitNesseTestHost.getInstance());
    }
 
    @Override
    protected ExpectedBehaviour getExpectedBehaviour() {
        if (!exceptionExpected) return ExpectedBehaviour.NO_EXCEPTION;
        if (!excNumberDefined) return ExpectedBehaviour.ANY_EXCEPTION;
        return ExpectedBehaviour.SPECIFIC_EXCEPTION;
    }

    @Override
    protected int getExpectedErrorCode() {
        return excNumberExpected;
    }
}
