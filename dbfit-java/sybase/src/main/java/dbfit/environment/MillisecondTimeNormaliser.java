package dbfit.environment;

import dbfit.util.TypeNormaliser;

import java.sql.Time;

public class MillisecondTimeNormaliser implements TypeNormaliser {

    @Override
    public Object normalise(Object o) {
        return (o == null) ? null : new MillisecondTime((Time) o);
    }
}
