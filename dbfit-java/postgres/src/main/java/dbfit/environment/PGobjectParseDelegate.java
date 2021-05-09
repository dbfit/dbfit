package dbfit.environment;

import org.postgresql.util.PGobject;

import java.sql.SQLException;

public class PGobjectParseDelegate {
    public static Object parse(String s) throws Exception {
        try {
            PGobject pGobject = new PGobject();
            pGobject.setType("JSONB");
            pGobject.setValue(s);
            return pGobject;
        }
        catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }
}
