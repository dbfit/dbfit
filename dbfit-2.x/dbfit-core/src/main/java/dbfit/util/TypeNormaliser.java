package dbfit.util;

import java.sql.SQLException;


public interface TypeNormaliser {
	public Object normalise(Object o) throws SQLException ;
}
