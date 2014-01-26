package dbfit.fixture;

import dbfit.fixture.report.*;
import dbfit.util.DataCell;
import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.DiffListener;
import static dbfit.util.MatchStatus.*;

import fit.Fixture;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompareStoredQueriesTest {

    @Mock ReportingSystem reportingSystem; // wrapper for reporting via fit.Fixture

    @Test
    public void shouldReportResultsToFixture() {
        DiffListener reporter = new CompareStoredQueries.FitFixtureReporter(
                reportingSystem);

        reporter.onEvent(createCellResultSuccess("*demo-1*"));

        verify(reportingSystem).cellRight("*demo-1*");
    }

    /*------ Setup helpers ----- */

    private MatchResult createCellResultSuccess(String val) {
        return createCellResult(val, val, SUCCESS, null);
    }

    private MatchResult createCellResultWrong(String s1, String s2) {
        return createCellResult(s1, s2, WRONG, null);
    }

    private MatchResult createCellException(String s1, String s2, Exception e) {
        return createCellResult(s1, s2, EXCEPTION, e);
    }

    @SuppressWarnings("unchecked")
    private MatchResult createCellResult(final String s1, final String s2,
            final MatchStatus status, final Exception ex) {

        DataCell o1 = new DataCell(null, null) {
            @Override public String toString() { return s1; }
        };

        DataCell o2 = new DataCell(null, null) {
            @Override public String toString() { return s2; }
        };

        MatchResult res = new MatchResult(o1, o2, status, DataCell.class);

        if (ex != null) {
            res.setException(ex);
        }

        return res;
    }

}
