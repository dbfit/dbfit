package dbfit;

import org.dbfit.postgre.PostgresEnvironment;
public class PostgresTest extends DatabaseTest {
	public PostgresTest(){
		super(new PostgresEnvironment());
	}
	public void dbfitDotPostgresTest() {
		// required by fitnesse release 20080812
	}
}
