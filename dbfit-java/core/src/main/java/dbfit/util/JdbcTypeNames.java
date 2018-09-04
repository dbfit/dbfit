package dbfit.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.sql.Types;

public class JdbcTypeNames {
    private static Map<Integer, String> typeNames = new HashMap<Integer, String>();

    static {
        for (Field field : Types.class.getFields()) {
            try {
                typeNames.put((Integer)field.get(null), field.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getTypeName(int typeNumber) {
        return typeNames.get(typeNumber);
    }
}
