package dbfit;

public class SnowflakeTest extends DatabaseTest {
    public SnowflakeTest(){
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Snowflake"));
    }
}

