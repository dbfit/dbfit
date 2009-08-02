package dbfit;

import dbfit.environment.*;;
public class SqlServerTest extends DatabaseTest {
	public SqlServerTest(){
		super(new SqlServerEnvironment());
	}

}
