package dbfit.test.matchers;

import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Factory;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.contains;

import java.util.ArrayList;

public class HasMatchStatus extends FeatureMatcher<MatchResult, MatchStatus> {

    public HasMatchStatus(Matcher<? super MatchStatus> matcher) {
        super(matcher, "with status", "status");
    }

    @Override
    public MatchStatus featureValueOf(MatchResult result) {
        return result.getStatus();
    }

    @Factory
    public static Matcher<MatchResult> hasMatchStatus(Matcher<? super MatchStatus> statusMatcher) {
        return new HasMatchStatus(statusMatcher);
    }

    @Factory
    public static Matcher<MatchResult> hasMatchStatus(MatchStatus expectedStatus) {
        return new HasMatchStatus(equalTo(expectedStatus));
    }

    @Factory
    public static Matcher<Iterable<? extends MatchResult>> haveItemsStatuses(
            Iterable<MatchStatus> expectedStatuses) {
        ArrayList<Matcher<? super MatchResult>> matchers = new ArrayList<>();

        for (MatchStatus status : expectedStatuses) {
            matchers.add(hasMatchStatus(status));
        }

        return contains(matchers);
    }
}
