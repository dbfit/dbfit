package dbfit;

import dbfit.environment.*;
public class PostgresTest extends DatabaseTest {
	public PostgresTest(){
		super(new PostgresEnvironment());
	}
}
