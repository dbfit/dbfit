package dbfit.test.matchers;

import fit.Parse;

import org.hamcrest.Matcher;
import org.hamcrest.Factory;
import static org.hamcrest.Matchers.*;

public class NumParsePartsThat extends TraversingParseMatcher {

    public NumParsePartsThat(int n, Matcher partMatcher) {
        super(partMatcher);
        withExpectedCount(n);
    }

    @Override
    protected void visitElements(Parse parse) {
        for (Parse part = parse.parts; part != null; part = part.more) {
            matchElement(part);
        }
    }

    @Override
    protected String getElementsKind() {
        return "part";
    }

    @Factory
    public static NumParsePartsThat numPartsThat(int n, Matcher matcher) {
        return new NumParsePartsThat(n, matcher);
    }
}
