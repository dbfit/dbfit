package dbfit.environment;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.rowset.OracleCachedRowSet;

import dbfit.util.TypeNormaliser;

public class OracleRefNormaliser implements TypeNormaliser {

    @Override
    public Object normalise(Object o) throws SQLException {
        if (o == null)
            return null;
        if (!(o instanceof ResultSet))
            throw new UnsupportedOperationException(
                    "OracleRefNormaliser cannot work with " + o.getClass());
        ResultSet rs = (ResultSet) o;
        OracleCachedRowSet ocrs = new OracleCachedRowSet();
        ocrs.populate(rs);
        return ocrs;
    }

}
