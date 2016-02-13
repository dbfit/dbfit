package dbfit.api;

import dbfit.util.Direction;

public interface DbParameterDescriptor {

    String getName();

    Direction getDirection();

    /**
     * Zero-based column or parameter position in a query, table or stored proc,
     * -1 for return values.
     */
    int getPosition();

    Class<?> getJavaType();

    String getUserDefinedTypeName();
}
