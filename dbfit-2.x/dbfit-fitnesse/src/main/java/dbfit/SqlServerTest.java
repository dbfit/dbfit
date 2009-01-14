package dbfit;

import org.dbfit.sqlserver.SqlServerEnvironment;
public class SqlServerTest extends DatabaseTest {
	public SqlServerTest(){
		super(new SqlServerEnvironment());
	}

}
