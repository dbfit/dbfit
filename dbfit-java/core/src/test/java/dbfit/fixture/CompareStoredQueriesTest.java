package dbfit.fixture;

import dbfit.fixture.report.ReportingSystem;
import dbfit.util.DataCell;
import dbfit.util.MatchResult;
import dbfit.util.DiffListener;
import static dbfit.util.MatchStatus.*;

import static dbfit.util.DiffTestUtils.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompareStoredQueriesTest {

    @Mock ReportingSystem reportingSystem; // wrapper for reporting via fit.Fixture

    @Test
    public void shouldReportCellSuccessToReportingSystem() {
        DiffListener reporter = new CompareStoredQueries.FitFixtureReporter(
                reportingSystem);

        MatchResult cellResult = createCellResult("*demo-1*", SUCCESS);

        reporter.onEvent(cellResult);

        verify(reportingSystem).addCell(cellResult);
    }
}
