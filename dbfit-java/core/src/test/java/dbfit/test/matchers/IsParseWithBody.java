package dbfit.test.matchers;

import fit.Parse;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Factory;
import static org.hamcrest.Matchers.equalTo;

public class IsParseWithBody extends FeatureMatcher<Parse, String> {
    public IsParseWithBody(Matcher<? super String> matcher) {
        super(matcher, "with body", "body");
    }

    @Override
    public String featureValueOf(Parse parse) {
        return parse.body;
    }

    @Factory
    public static Matcher<Parse> hasBodyThat(Matcher<? super String> matcher) {
        return new IsParseWithBody(matcher);
    }

    @Factory
    public static Matcher<Parse> hasBody(String actualBody) {
        return new IsParseWithBody(equalTo(actualBody));
    }
}
