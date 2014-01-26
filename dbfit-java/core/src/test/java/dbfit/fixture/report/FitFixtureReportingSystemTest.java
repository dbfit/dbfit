package dbfit.fixture.report;

// Test setup utilities
import static dbfit.util.DiffTestUtils.*;

import static dbfit.util.MatchStatus.*;

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

import org.apache.commons.lang3.ObjectUtils;

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

    @Test
    public void shouldAddCellWithClassRightToOutput() {
        reportingSystem.addCell(createCellResult("*cell-demo-1*", SUCCESS));
        reportingSystem.endRow(createNullRowResult(SUCCESS)); // finalize the row

        assertThat(table.body, hasToString("*cell-demo-1*"));
    }

    /*------ Setup helpers ----- */

    private Parse createTestTableParse() throws Exception {
        return new Parse("<table>" +
            "<tr><td>Fake Fixture</td></tr>" +
            "<tr><td>arg1</td><td>arg2</td></tr>" +
            "</table>");
    }
}
