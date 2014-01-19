package dbfit.util;

public class MatchResult<T1, T2> {
    protected T1 object1;
    protected T2 object2;
    protected MatchStatus status;

    public MatchResult(T1 object1, T2 object2, MatchStatus status) {
        this.object1 = object1;
        this.object2 = object2;
        this.status = status;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public T1 getObject1() {
        return object1;
    }

    public T2 getObject2() {
        return object2;
    }

    public boolean isMatching() {
        return status == MatchStatus.SUCCESS;
    }
}

