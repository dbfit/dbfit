package dbfit;

import org.dbfit.postgre.PostgresEnvironment;
public class PostgresTest extends DatabaseTest {
	public PostgresTest(){
		super(new PostgresEnvironment());
	}
}
