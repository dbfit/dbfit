package dbfit.util;

public interface MatcherListener<T1, T2> {
    public void endRow(MatchResult<T1, T2> result);
}

