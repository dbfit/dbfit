package dbfit.util;

import static dbfit.util.MatchStatus.*;

import java.util.Objects;

public class MatchResult<T1, T2> {
    protected final T1 object1;
    protected final T2 object2;
    protected final Class type;
    protected MatchStatus status;
    protected Exception exception = null;

    public MatchResult(T1 object1, T2 object2, MatchStatus status, Class type,
                Exception ex) {
        this(object1, object2, status, type);
        setException(ex);
    }

    public MatchResult(T1 object1, T2 object2, MatchStatus status, Class type) {
        this.object1 = object1;
        this.object2 = object2;
        this.status = status;
        this.type = type;
    }

    public static <T1, T2> MatchResult<T1, T2> create(T1 object1, T2 object2,
            Class type) {
        return create(object1, object2, UNVERIFIED, type);
    }

    public static <T1, T2> MatchResult<T1, T2> create(T1 object1, T2 object2,
            MatchStatus status, Class type) {
        return new MatchResult<T1, T2>(object1, object2, status, type);
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public T1 getObject1() {
        return object1;
    }

    public T2 getObject2() {
        return object2;
    }

    public String getStringValue1() {
        return Objects.toString(object1, null);
    }

    public String getStringValue2() {
        return Objects.toString(object2, null);
    }

    public void setException(Exception exception) {
        this.exception = exception;
        this.status = (null == exception) ? status : EXCEPTION;
    }

    public Exception getException() {
        return exception;
    }

    public Class getType() {
        return type;
    }

    public boolean isMatching() {
        return status == SUCCESS;
    }
}
