package dbfit.test.matchers;

import fit.Parse;

import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Factory;
import static org.hamcrest.Matchers.anything;

import java.io.StringWriter;
import java.io.PrintWriter;

public class IsParseThat extends TypeSafeMatcher<Parse> {
    protected Matcher matcher;
    protected boolean recursiveChildren = false;
    protected boolean recursiveSiblings = false;

    private int expectedCount = 1;
    private int actualCount = 0;

    public IsParseThat() {
        this(anything());
    }

    public IsParseThat(Matcher matcher) {
        this.matcher = matcher;
    }

    public IsParseThat which(Matcher matcher) {
        this.matcher = matcher;
        return this;
    }

    public IsParseThat withRecursiveChildren() {
        recursiveChildren = true;
        return this;
    }

    public IsParseThat withRecursiveSiblings() {
        recursiveSiblings = true;
        return this;
    }

    public IsParseThat withExpectedCount(int expectedCount) {
        this.expectedCount = expectedCount;
        return this;
    }

    protected void countMatches(final Parse parse) {
        if (parse == null) {
            return;
        }

        if (matcher.matches(parse)) {
            actualCount++;
        }

        if (recursiveChildren) {
            countMatches(parse.parts);
        }

        if (recursiveSiblings) {
            countMatches(parse.more);
        }
    }

    @Override
    public boolean matchesSafely(Parse parse) {
        countMatches(parse);
        return (actualCount == expectedCount);
    }

    @Override
    public void describeTo(final Description description) {
        description
            .appendText(String.format("should contain %d cells", expectedCount))
            .appendText(" where:\n");
        description.appendDescriptionOf(matcher);
    }

    @Override
    public void describeMismatchSafely(Parse item, Description mismatchDescription) {
        StringWriter sw = new StringWriter();
        item.print(new PrintWriter(sw));
        mismatchDescription
            .appendText("was actualCount=" + actualCount + "\n:\"")
            .appendText(sw.toString()).appendText("\"");
    }

    @Factory
    public static IsParseThat isParseThat() {
        return new IsParseThat();
    }

    @Factory
    public static IsParseThat isParseThat(Matcher matcher) {
        return new IsParseThat(matcher);
    }
}
