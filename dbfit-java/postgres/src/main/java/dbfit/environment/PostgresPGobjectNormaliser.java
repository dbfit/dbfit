package dbfit.environment;

import dbfit.util.TypeTransformer;
import org.postgresql.util.PGobject;

import java.sql.SQLException;

public class PostgresPGobjectNormaliser implements TypeTransformer {

    @Override
    public Object transform(Object o) throws SQLException {
        if (o == null)
            return null;
        if (!(o instanceof PGobject)) {
            throw new UnsupportedOperationException(
                    "PostgresPGobjectNormaliser cannot work with "
                            + o.getClass());
        }
        PGobject pGobject = (PGobject) o;
        return pGobject.getValue();
    }
}
