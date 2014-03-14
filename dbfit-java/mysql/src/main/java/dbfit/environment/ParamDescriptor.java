package dbfit.environment;

import dbfit.util.Direction;
import static dbfit.util.Direction.*;

public class ParamDescriptor {
    public final String name;
    public final Direction direction;
    public final String type;

    public ParamDescriptor(String name, Direction direction, String type) {
        this.name = name;
        this.direction = direction;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", name, direction, type);
    }
}
