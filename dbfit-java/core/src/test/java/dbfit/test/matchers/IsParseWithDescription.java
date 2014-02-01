package dbfit.test.matchers;

import fit.Parse;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Factory;

public class IsParseWithDescription extends FeatureMatcher<Parse, String> {
    public IsParseWithDescription(Matcher<? super String> matcher) {
        super(matcher, "with row description", "row description");
    }

    @Override
    public String featureValueOf(Parse row) {
        return row.parts.leaf().body;
    }

    @Factory
    public static Matcher<Parse> hasDescriptionThat(Matcher<? super String> matcher) {
        return new IsParseWithDescription(matcher);
    }
}
