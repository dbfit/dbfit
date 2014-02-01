package dbfit.test.matchers;

import fit.Parse;

import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.anything;

import java.io.StringWriter;
import java.io.PrintWriter;

public class TraversingParseMatcher extends TypeSafeMatcher<Parse> {
    protected Matcher elementMatcher;
    protected int expectedCount = 1;
    protected int actualCount = 0;

    // Override this to traverse sub-elements
    protected void visitElements(Parse parse) {
        matchElement(parse);
    }

    public TraversingParseMatcher() {
        this(anything());
    }

    public TraversingParseMatcher(Matcher elementMatcher) {
        this.elementMatcher = elementMatcher;
    }

    public TraversingParseMatcher which(Matcher elementMatcher) {
        this.elementMatcher = elementMatcher;
        return this;
    }

    public TraversingParseMatcher withExpectedCount(int expectedCount) {
        this.expectedCount = expectedCount;
        return this;
    }

    protected void matchElement(Parse element) {
        if (elementMatcher.matches(element)) {
            ++actualCount;
        }
    }

    private final void matchElements(Parse parse) {
        actualCount = 0;
        visitElements(parse);
    }

    @Override
    public boolean matchesSafely(Parse parse) {
        matchElements(parse);
        return (actualCount == expectedCount);
    }

    @Override
    public void describeTo(final Description description) {
        description
            .appendText("should contain ")
            .appendText(expectedCount + " " + getElementsKind() + "s")
            .appendText(" where:\n")
            .appendDescriptionOf(elementMatcher);
    }

    @Override
    public void describeMismatchSafely(Parse item, Description mismatchDescription) {
        StringWriter sw = new StringWriter();
        item.print(new PrintWriter(sw));
        mismatchDescription
            .appendText("was actualCount=" + actualCount + ":\n---\n")
            .appendText(sw.toString()).appendText("\n---\n");
    }

    protected String getElementsKind() {
        return "element";
    }

}
