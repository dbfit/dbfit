package dbfit.fixture.report;

// Test setup utilities
import static dbfit.util.DiffTestUtils.*;

import static dbfit.util.MatchStatus.*;
import dbfit.util.MatchStatus;

import fit.Fixture;
import fit.Parse;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertThat;

import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import static org.hamcrest.Matchers.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import org.apache.commons.lang3.ObjectUtils;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FitFixtureReportingSystemTest {

    // The system under test
    private FitFixtureReportingSystem reportingSystem;

    @Mock private Fixture fixture;
    private Parse table;
    private ArgumentCaptor<Parse> captor;

    @Before
    public void prepare() throws Exception {
        table = createTestTableParse();
        reportingSystem = new FitFixtureReportingSystem(fixture, table);
        captor = ArgumentCaptor.forClass(Parse.class);
    }

    @Test
    public void shouldCallFixtureRightOnCellRight() {
        reportingSystem.addCell(createCellResult("*cell-demo-1*", SUCCESS));

        verify(fixture).right(captor.capture());
        assertThat(captor.getValue().body, hasToString("*cell-demo-1*"));
    }

    private void addCell(String text, MatchStatus status) {
        reportingSystem.addCell(createCellResult(text, status));
    }

    @Test
    public void shouldAddCellWithClassPassToOutput() {
        reportingSystem = new FitFixtureReportingSystem(new Fixture(), table);

        reportingSystem.addCell(createCellResult("*cell-demo-1*", SUCCESS));

        assertThat(table, new NumberOfCellsWith(1, "*cell-demo-1*", "pass"));
    }

    @Test
    public void shouldAddCellWithClassFailToOutput() {
        reportingSystem = new FitFixtureReportingSystem(new Fixture(), table);

        reportingSystem.addCell(createCellResult("*GOOD-1*", "*BAD-2*", WRONG));

        assertThat(table, new NumberOfCellsWith(1, "*GOOD-1*", "fail"));
    }

    @Test
    public void shouldReportMissingRows() {
        reportingSystem = new FitFixtureReportingSystem(new Fixture(), table);

        reportingSystem.addCell(createCellResult("*S-1*", null, SUCCESS));
        reportingSystem.addCell(createCellResult("*S-2*", null, SUCCESS));
        reportingSystem.endRow(createNullRowResult(SUCCESS));

        reportingSystem.addCell(createCellResult("*M-1*", null, MISSING));
        reportingSystem.addCell(createCellResult("*M-2*", null, MISSING));
        reportingSystem.endRow(createNullRowResult(MISSING));

        assertThat(table, new NumRowsWithDescription(1, "missing", "fail"));
    }

    @Test
    public void shouldReportSurplusRows() {
        reportingSystem = new FitFixtureReportingSystem(new Fixture(), table);

        reportingSystem.addCell(createCellResult("*M-1*", null, MISSING));
        reportingSystem.endRow(createNullRowResult(MISSING));
        reportingSystem.addCell(createCellResult(null, "*S-1*", SURPLUS));
        reportingSystem.endRow(createNullRowResult(SURPLUS));

        assertThat(table, new NumRowsWithDescription(1, "surplus", "fail"));
        assertThat(table, new NumberOfCellsWith(1, "*S-1*", "fail"));
    }

    @Test
    public void shouldReportExceptionCells() {
        reportingSystem = new FitFixtureReportingSystem(new Fixture(), table);

        reportingSystem.addCell(createCellException("*E-1*", "*E-1*",
                    new Exception("Cruel World!")));
        reportingSystem.endRow(createNullRowResult(EXCEPTION));

        assertThat(table, new NumberOfCellsWith(1, "*E-1*", "error"));
        assertThat(table, new ParseThat()
                       .withRecursiveChildren()
                       .withRecursiveSiblings()
                       .withTagThat(containsString("<td"))
                       .which(new ParseThat().withBodyThat(allOf(
                                   containsString("Cruel World!"),
                                   containsString("stacktrace")))));
    }

    /*------ Custom matchers ----- */

    public static class NumRowsWithDescription extends TypeSafeMatcher<Parse> {
        protected int expectedCount;
        protected String expectedDescription;
        protected int actualCount;
        private String tagClass;

        public NumRowsWithDescription(int n, String descr, String tagClass) {
            this.expectedCount = n;
            this.expectedDescription = descr;
            this.tagClass = tagClass;
        }

        private boolean rowMatches(Parse row) {
            String tag = row.tag;
            String descr = row.parts.leaf().body;
            return tag.contains(tagClass) && descr.contains(expectedDescription);
        }

        @Override
        public boolean matchesSafely(Parse table) {
            int numMatches = 0;

            for (Parse row = table.parts; row != null; row = row.more) {
                if (rowMatches(row)) {
                    ++numMatches;
                }
            }

            actualCount = numMatches;
            return (expectedCount == numMatches);
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText(String.format(
                    "should contain %d '%s' rows with description '%s' ",
                    expectedCount, tagClass, expectedDescription));
        }

        @Override
        public void describeMismatchSafely(Parse item, Description mismatchDescription) {
            StringWriter sw = new StringWriter();
            item.print(new PrintWriter(sw));
            mismatchDescription
                .appendText("was actualCount=" + actualCount + "\n:\"")
                .appendText(sw.toString()).appendText("\"");
        }
    }

    public static class NumberOfCellsWith extends TypeSafeMatcher<Parse> {
        private String text;
        private String tagClass;
        private int expectedCount;

        private int actualCount = 0;

        public NumberOfCellsWith(int n, String text, String tagClass) {
            this.text = text;
            this.tagClass = tagClass;
            this.expectedCount = n;
        }

        private boolean valuesMatch(String body, String tag) {
            body = ObjectUtils.toString(body, "");
            tag = ObjectUtils.toString(tag, "");
            return body.contains(text) && tag.contains(tagClass);
        }

        private boolean cellMatches(Parse cell) {
            if (cell == null) {
                return false;
            } else {
                return valuesMatch(cell.body, cell.tag)
                            || cellMatches(cell.parts)
                            || cellMatches(cell.more);
            }
        }

        @Override
        public boolean matchesSafely(Parse table) {
            int numMatches = 0;

            for (Parse row = table.parts; row != null; row = row.more ) {
                for (Parse cell = row.parts; cell != null; cell = cell.more) {
                    if (cellMatches(cell)) {
                        ++numMatches;
                    }
                }
            }

            actualCount = numMatches;
            return (numMatches == expectedCount);
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText(String.format(
                    "should contain %d cells with body '%s' and tag class '%s' ",
                    expectedCount, text, tagClass));
        }

        @Override
        public void describeMismatchSafely(Parse item, Description mismatchDescription) {
            StringWriter sw = new StringWriter();
            item.print(new PrintWriter(sw));
            mismatchDescription
                .appendText("was actualCount=" + actualCount + "\n:\"")
                .appendText(sw.toString()).appendText("\"");
        }
    }

    public static class ParseThat extends TypeSafeMatcher<Parse> {
        protected Matcher bodyMatcher = anything("any body");
        protected Matcher tagMatcher = anything("any tag");
        protected Matcher partsMatcher = anything("any parts");
        protected Matcher siblingsMatcher = anything("any siblings");

        protected List<Matcher> matchers = new ArrayList<Matcher>();

        protected boolean recursiveChildren = false;
        protected boolean recursiveSiblings = false;

        private int expectedCount = 1;
        private int actualCount = 0;

        public ParseThat which(Matcher matcher) {
            matchers.add(matcher);
            return this;
        }

        public ParseThat withRecursiveChildren() {
            recursiveChildren = true;
            return this;
        }

        public ParseThat withRecursiveSiblings() {
            recursiveSiblings = true;
            return this;
        }

        public ParseThat withExpectedCount(int expectedCount) {
            this.expectedCount = expectedCount;
            return this;
        }

        public ParseThat withBodyThat(Matcher bodyMatcher) {
            this.bodyMatcher = bodyMatcher;
            return this;
        }

        public ParseThat withTagThat(Matcher tagMatcher) {
            this.tagMatcher = tagMatcher;
            return this;
        }

        public ParseThat withPartsThat(Matcher partsMatcher) {
            this.partsMatcher = partsMatcher;
            return this;
        }

        protected boolean satisfiesAdditionalMatchers(Parse parse) {
            for (Matcher matcher: matchers) {
                if (!matcher.matches(parse)) {
                    return false;
                }
            }

            return true;
        }

        protected void countMatches(final Parse parse) {
            if (parse == null) {
                return;
            }

            if (bodyMatcher.matches(parse.body) &&
                    tagMatcher.matches(parse.tag) &&
                    satisfiesAdditionalMatchers(parse)) {
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
                .appendText(" where body is ");
            bodyMatcher.describeTo(description);
            description.appendText("; tag is ");
            tagMatcher.describeTo(description);
        }

        @Override
        public void describeMismatchSafely(Parse item, Description mismatchDescription) {
            StringWriter sw = new StringWriter();
            item.print(new PrintWriter(sw));
            mismatchDescription
                .appendText("was actualCount=" + actualCount + "\n:\"")
                .appendText(sw.toString()).appendText("\"");
        }
    }

    /*------ Setup helpers ----- */

    private Parse createTestTableParse() throws Exception {
        return new Parse("<table>" +
            "<tr><td>Fake Fixture</td></tr>" +
            "<tr><td>arg1</td><td>arg2</td></tr>" +
            "</table>");
    }
}
