package dbfit;

public class PostgresTest extends DatabaseTest {
    public PostgresTest(){
        super(new PostgresEnvironment());
    }
}

