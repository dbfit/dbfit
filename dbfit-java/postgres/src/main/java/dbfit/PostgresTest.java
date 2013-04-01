package dbfit;

import dbfit.environment.PostgresEnvironment;

public class PostgresTest extends DatabaseTest {
    public PostgresTest(){
        super(new PostgresEnvironment());
    }
}

