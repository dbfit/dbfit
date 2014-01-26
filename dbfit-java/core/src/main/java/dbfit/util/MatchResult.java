package dbfit.util;

import org.apache.commons.lang3.ObjectUtils;

public class MatchResult<T1, T2> {
    protected T1 object1;
    protected T2 object2;
    protected MatchStatus status;
    protected Exception exception = null;
    protected Class type;

    public MatchResult(T1 object1, T2 object2, MatchStatus status, Class type) {
        this.object1 = object1;
        this.object2 = object2;
        this.status = status;
        this.type = type;
    }

    public static <T1, T2> MatchResult<T1, T2> create(T1 object1, T2 object2,
            Class type) {
        return create(object1, object2, MatchStatus.UNVERIFIED, type);
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
        return ObjectUtils.toString(object1, null);
    }

    public String getStringValue2() {
        return ObjectUtils.toString(object2, null);
    }

    public void setException(Exception exception) {
        this.exception = exception;
        if (null != exception) {
            setStatus(MatchStatus.EXCEPTION);
        }
    }

    public Exception getException() {
        return exception;
    }

    public Class getType() {
        return type;
    }

    public void setType(final Class type) {
        this.type = type;
    }

    public boolean isMatching() {
        return status == MatchStatus.SUCCESS;
    }
}

