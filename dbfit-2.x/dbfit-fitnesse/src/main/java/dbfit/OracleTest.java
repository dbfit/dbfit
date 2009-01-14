package dbfit;

import org.dbfit.oracle.OracleEnvironment;

public class OracleTest extends DatabaseTest {
	public OracleTest() {
		super(new OracleEnvironment());
	}

}
