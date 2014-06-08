package dbfit.environment;

import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * Wrapper of Time overriding toString in order to get milliseconds
 * (java.sql.Time defaults to second-level only)
 */
class MillisecondTime extends Time {
    public MillisecondTime(final Time time) {
        this(time.getTime());
    }

    public MillisecondTime(long time) {
        super(time);
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("HH:mm:ss.S").format(this);
    }
}
