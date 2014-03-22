package dbfit.util;

import static dbfit.util.Direction.*;
import static dbfit.util.NameNormaliser.*;

import java.util.Map;
import java.util.HashMap;

public class DbParameterAccessorsMapBuilder {
    private Map<String, DbParameterAccessor> parameterAccessors = new HashMap<>();
    private int nextPosition = 0;

    public Map<String, DbParameterAccessor> toMap() {
        return parameterAccessors;
    }

    public void add(String name, Direction direction, int sqlType, Class javaType) {
        int position = (direction == RETURN_VALUE) ? -1 : nextPosition++;
        parameterAccessors.put(normaliseName(name), new DbParameterAccessor(
                    name, direction,sqlType, javaType, position));
    }
}
