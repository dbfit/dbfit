package dbfit.fixture.report;

// Test utilities
import dbfit.test.matchers.*;
import static dbfit.test.matchers.IsParseWithTag.*;
import static dbfit.test.matchers.IsParseWithBody.*;
import static dbfit.test.matchers.IsParseThat.*;
import static dbfit.test.matchers.IsParseWithDescription.*;
import static dbfit.test.matchers.NumRowsWithDescription.*;
import static dbfit.test.matchers.NumParsePartsThat.*;
import static dbfit.test.matchers.NumCellsThat.*;
import static dbfit.util.DiffTestUtils.*;

import static dbfit.util.MatchStatus.*;
import dbfit.util.MatchStatus;

import fit.Fixture;
import fit.Parse;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertThat;

import static org.hamcrest.Matchers.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

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

        assertThat(table, numCellsWith(1, "*cell-demo-1*", "pass"));
    }

    @Test
    public void shouldAddCellWithClassFailToOutput() {
        reportingSystem = new FitFixtureReportingSystem(new Fixture(), table);

        reportingSystem.addCell(createCellResult("*GOOD-1*", "*BAD-2*", WRONG));

        assertThat(table, numCellsWith(1, "*GOOD-1*", "fail"));
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

        assertThat(table, numRowsWithDescription(1, "missing", "fail"));
    }

    @Test
    public void shouldReportSurplusRows() {
        reportingSystem = new FitFixtureReportingSystem(new Fixture(), table);

        reportingSystem.addCell(createCellResult("*M-1*", null, MISSING));
        reportingSystem.endRow(createNullRowResult(MISSING));
        reportingSystem.addCell(createCellResult(null, "*S-1*", SURPLUS));
        reportingSystem.endRow(createNullRowResult(SURPLUS));

        assertThat(table, numPartsThat(1, allOf(
                hasTagThat(allOf(
                        containsString("<tr"),
                        containsString("fail"))),
                hasDescriptionThat(containsString("surplus")),
                numPartsThat(1, allOf(
                        hasTagThat(allOf(
                                containsString("<td"),
                                containsString("fail"))),
                        hasBodyThat(containsString("*S-1*")))))));
    }

    @Test
    public void shouldReportExceptionCells() {
        reportingSystem = new FitFixtureReportingSystem(new Fixture(), table);

        reportingSystem.addCell(createCellException("*E-1*", "*E-1*",
                    new Exception("Cruel World!")));
        reportingSystem.endRow(createNullRowResult(EXCEPTION));

        assertThat(table, numCellsWith(1, "*E-1*", "error"));
        assertThat(table, isParseThat()
                       .withRecursiveChildren()
                       .withRecursiveSiblings()
                       .which(allOf(
                               hasTagThat(containsString("<td")),
                               hasBodyThat(containsString("Cruel World!")),
                               hasBodyThat(containsString("stacktrace")))));
    }

    @Test
    public void testAddExceptionOnStart() {
        reportingSystem = new FitFixtureReportingSystem(new Fixture(), table);

        reportingSystem.addException(new Exception("Cruel World!"));

        assertThat(table, isParseThat()
                       .withRecursiveChildren()
                       .withRecursiveSiblings()
                       .which(allOf(
                           hasTagThat(containsString("<td")),
                           hasBodyThat(allOf(
                                   containsString("Cruel World!"),
                                   containsString("stacktrace"))))));
    }

    @Test
    public void testAddExceptionWhileInTheMiddleOfRow() {
        reportingSystem = new FitFixtureReportingSystem(new Fixture(), table);
        reportingSystem.addCell(createCellResult("*S-1*", SUCCESS));

        reportingSystem.addException(new Exception("Cruel World!"));

        assertThat(table, isParseThat()
                       .withRecursiveChildren()
                       .withRecursiveSiblings()
                       .which(allOf(
                               hasTagThat(containsString("<td")),
                               hasBodyThat(allOf(
                                       containsString("Cruel World!"),
                                       containsString("stacktrace"))))));
    }

    /*------ Setup helpers ----- */

    private Parse createTestTableParse() throws Exception {
        return new Parse("<table>" +
            "<tr><td>Fake Fixture</td></tr>" +
            "<tr><td>arg1</td><td>arg2</td></tr>" +
            "</table>");
    }
}
