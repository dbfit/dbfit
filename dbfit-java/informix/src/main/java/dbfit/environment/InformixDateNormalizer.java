package dbfit.environment;

import dbfit.util.TypeNormaliser;

import java.sql.SQLException;

/**
 * Created by muppana on 7/1/2014.
 */
public class InformixDateNormalizer implements TypeNormaliser {

    @Override
    public Object normalise(Object o) throws SQLException {
        if (o == null)
            return null;
        if (!(o instanceof  com.informix.jdbc.IfxDate)) {
            throw new UnsupportedOperationException(
                    "OracleDateNormaliser cannot work with " + o.getClass());
        }
        com.informix.jdbc.IfxDate ts = ( com.informix.jdbc.IfxDate) o;
        return ts.value();


    }
}
