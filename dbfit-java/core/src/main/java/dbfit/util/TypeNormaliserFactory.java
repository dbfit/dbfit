package dbfit.util;

import java.sql.SQLException;

public class TypeNormaliserFactory {
    private static TypeTransformerFactory normaliserFactory = new TypeTransformerFactory();

    public static void setNormaliser(Class<?> targetClass, TypeTransformer normaliser) {
        normaliserFactory.setTransformer(targetClass, normaliser);
    }

    public static Object transform(Object value) throws SQLException {
        return normaliserFactory.transform(value);
    }
}
