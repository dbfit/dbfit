package dbfit;
import dbfit.environment.MySqlEnvironment;
public class MySqlTest extends DatabaseTest {
	public MySqlTest(){
		super(new MySqlEnvironment());
	}
}
