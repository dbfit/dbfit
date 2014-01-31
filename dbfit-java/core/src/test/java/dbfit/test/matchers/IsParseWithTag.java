package dbfit.test.matchers;

import fit.Parse;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Factory;

public class IsParseWithTag extends FeatureMatcher<Parse, String> {
    public IsParseWithTag(Matcher<? super String> matcher) {
        super(matcher, "with tag", "tag");
    }

    @Override
    public String featureValueOf(Parse parse) {
        return parse.tag;
    }

    @Factory
    public static Matcher<Parse> hasTagThat(Matcher<? super String> matcher) {
        return new IsParseWithTag(matcher);
    }
}
