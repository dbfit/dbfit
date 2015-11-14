package dbfit.environment;

import dbfit.util.TypeTransformer;

import java.sql.Time;

public class MillisecondTimeNormaliser implements TypeTransformer {

    @Override
    public Object transform(Object o) {
        return (o == null) ? null : new MillisecondTime((Time) o);
    }
}
