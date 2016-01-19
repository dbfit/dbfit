package dbfit.util;

import java.sql.SQLException;

public class ValueNormaliser {

    public static Object normaliseValue(Object value) throws SQLException {
        if (value == null) {
            return null;
        }

        TypeTransformer transformer =
            TypeNormaliserFactory.getNormaliser(value.getClass());

        return (transformer == null) ? value : transformer.transform(value);
    }
}
