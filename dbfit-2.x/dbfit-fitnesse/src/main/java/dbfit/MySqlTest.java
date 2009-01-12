package dbfit;

import org.dbfit.mysql.MySqlEnvironment;
public class MySqlTest extends DatabaseTest {
	public MySqlTest(){
		super(new MySqlEnvironment());
	}
	public void dbfitDotMySqlTest() {
		// required by fitnesse release 20080812
	}
}
