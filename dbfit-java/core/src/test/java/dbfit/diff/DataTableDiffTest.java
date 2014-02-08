package dbfit.diff;

import static dbfit.util.DiffTestUtils.*;
import static dbfit.test.matchers.HasMatchStatus.*;

import dbfit.util.DataTable;
import dbfit.util.DataRow;
import dbfit.util.MatchResult;
import dbfit.util.DiffListenerAdapter;
import dbfit.util.DiffHandler;
import dbfit.util.RowStructure;
import dbfit.util.MatchStatus;
import static dbfit.util.MatchStatus.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import static java.util.Arrays.asList;

@RunWith(MockitoJUnitRunner.class)
public class DataTableDiffTest {

    private RowStructure rowStructure = new RowStructure(
            new String[] { "n", "2n" }, /* names */
            new boolean[] { true, false } /* keys */
        );

    @Mock private DiffHandler handler;
    @Captor ArgumentCaptor<MatchResult<DataRow, DataRow>> rowResultCaptor;
    @Captor ArgumentCaptor<MatchResult<DataTable, DataTable>> tabResultCaptor;

    private DataTableDiff diff;

    DataRow r1 = createRow(1, 2);
    DataRow r2 = createRow(2, 4);
    DataRow r3 = createRow(3, 6);
    DataRow r4 = createRow(4, 8);
    DataRow r5 = createRow(5, 10);
    DataRow b2 = createRow(2, 44);

    @Before
    public void prepare() {
        diff = new DataTableDiff(rowStructure);
        diff.addListener(new DiffListenerAdapter(handler));
    }

    @Test
    public void testMismatchWithRightWrongSurplusAndMissing() {
        diff.diff(createDt(r1, r2, r3), createDt(r1, b2, r4));
        verifyRowStatuses(SUCCESS, WRONG, MISSING, SURPLUS);
    }

    @Test
    public void shouldEmitSummaryTableEventOnMismatchingDiff() {
        diff.diff(createDt(r1, r2, r3), createDt(r1, b2, r4));
        verifyTabStatus(WRONG);
    }

    @Test
    public void shouldEmitSummaryTableEventOnMatchingDiff() {
        diff.diff(createDt(r1, r2, r3), createDt(r3, r1, r2));
        verifyTabStatus(SUCCESS);
    }

    @Test
    public void testSingleWrongRow() {
        diff.diff(createDt(r2), createDt(b2));
        verifyRowStatuses(WRONG);
    }

    private void verifyRowStatuses(MatchStatus... expectedStatuses) {
        verify(handler, times(expectedStatuses.length)).endRow(
                rowResultCaptor.capture());

        assertThat(statusesOf(rowResultCaptor.getAllValues()),
                equalTo(asList(expectedStatuses)));
    }

    private void verifyTabStatus(MatchStatus expectedStatus) {
        verify(handler).endTable(tabResultCaptor.capture());
        assertThat(tabResultCaptor.getValue(), hasMatchStatus(expectedStatus));
    }

    private DataRow createRow(Integer... items) {
        return createDataRowBuilder(rowStructure).createRow(items);
    }

    private DataTable createDt(DataRow... rows) {
        return createDataTable(rowStructure, rows);
    }
}
