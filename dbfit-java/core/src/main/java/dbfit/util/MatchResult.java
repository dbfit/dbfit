package dbfit.util;

public class MatchResult<T1, T2> {
    protected T1 object1;
    protected T2 object2;
    protected MatchStatus status;
    protected Exception exception = null;

    public MatchResult(T1 object1, T2 object2, MatchStatus status) {
        this.object1 = object1;
        this.object2 = object2;
        this.status = status;
    }

    public static <T1, T2> MatchResult<T1, T2> create(T1 object1, T2 object2) {
        return create(object1, object2, MatchStatus.UNVERIFIED);
    }

    public static <T1, T2> MatchResult<T1, T2> create(T1 object1, T2 object2,
            MatchStatus status) {
        return new MatchResult<T1, T2>(object1, object2, status);
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

    public void setException(Exception exception) {
        this.exception = exception;
        if (null != exception) {
            setStatus(MatchStatus.EXCEPTION);
        }
    }

    public boolean isMatching() {
        return status == MatchStatus.SUCCESS;
    }

}

