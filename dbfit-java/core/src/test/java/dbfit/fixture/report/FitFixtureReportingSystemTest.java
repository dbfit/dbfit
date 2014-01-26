package dbfit.fixture.report;

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
    public void testCellRight() {
        reportingSystem.cellRight("val-1");

        verify(fixture).right(captor.capture());
        assertThat(captor.getValue().body, hasToString("val-1"));
    }

    @Test
    public void testCellWrong() {
        reportingSystem.cellRight("val-1");

        verify(fixture).right(captor.capture());
        assertThat(captor.getValue().body, hasToString("val-1"));
    }

    /*------ Setup helpers ----- */

    private Parse createTestTableParse() throws Exception {
        return new Parse("<table>" +
            "<tr><td>Fake Fixture</td></tr>" +
            "<tr><td>arg1</td><td>arg2</td></tr>" +
            "</table>");
    }
}
