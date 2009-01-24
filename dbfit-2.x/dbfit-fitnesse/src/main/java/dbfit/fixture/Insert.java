package dbfit.fixture;

import java.sql.SQLException;
import org.dbfit.core.DBEnvironment;
import org.dbfit.core.DbObject;
import org.dbfit.core.DbTable;

public class Insert extends DbObjectExecutionFixture{
	
	
	private DBEnvironment environment;
	private String tableName;
	public Insert() {
	}

	public Insert(DBEnvironment dbEnvironment) {
		this.environment = dbEnvironment;
	}

	public Insert(DBEnvironment dbEnvironment, String tableName) {
		this.tableName = tableName;
		this.environment = dbEnvironment;
	}
	@Override
	protected DbObject getTargetDbObject() throws SQLException {
		if ((tableName == null || tableName.trim().length() == 0)
				&& args.length > 0) {
			tableName = args[0];
		};
		return new DbTable(environment, tableName);
	}
}
