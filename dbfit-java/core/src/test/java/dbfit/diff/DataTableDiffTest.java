package dbfit.diff;

import static dbfit.util.DiffTestUtils.*;

import dbfit.util.DataTable;
import dbfit.util.DataRow;
import dbfit.util.MatchResult;
import dbfit.util.DiffListenerAdapter;
import dbfit.util.DiffHandler;
import dbfit.util.RowStructure;
import static dbfit.util.MatchStatus.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DataTableDiffTest {

    @Mock private DiffHandler handler;
    private dbfit.util.RowStructure rowStructure = new RowStructure(
            new String[] { "n", "2n" }, /* names */
            new boolean[] { true, false } /* keys */
        );
    private ArgumentCaptor<MatchResult> captor;
    private DataTableDiff diff;

    DataRow r1 = createRow(1, 2);
    DataRow r2 = createRow(2, 4);
    DataRow r3 = createRow(3, 6);
    DataRow r4 = createRow(4, 8);
    DataRow r5 = createRow(5, 10);
    DataRow b2 = createRow(2, 44);

    @Before
    public void prepare() {
        captor = ArgumentCaptor.forClass(MatchResult.class);
        diff = createDiff();
    }

    @SuppressWarnings("unchecked")
    private void runDiff(DataTable dt1, DataTable dt2) {
        diff.diff(dt1, dt2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMismatchWithRightWrongSurplusAndMissing() {
        runDiff(createDt(r1, r2, r3), createDt(r1, b2, r4));

        verify(handler, times(4)).endRow(captor.capture());
        List<MatchResult> rowMatches = captor.getAllValues();

        assertEquals(SUCCESS, rowMatches.get(0).getStatus());
        assertEquals(WRONG, rowMatches.get(1).getStatus());
        assertEquals(MISSING, rowMatches.get(2).getStatus());
        assertEquals(SURPLUS, rowMatches.get(3).getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldEmitSummaryTableEventOnMismatchingDiff() {
        runDiff(createDt(r1, r2, r3), createDt(r1, b2, r4));

        verify(handler, times(1)).endTable(captor.capture());
        assertThat(captor.getValue().getStatus(), is(WRONG));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldEmitSummaryTableEventOnMatchingDiff() {
        runDiff(createDt(r1, r2, r3), createDt(r3, r1, r2));

        verify(handler, times(1)).endTable(captor.capture());
        assertThat(captor.getValue().getStatus(), is(SUCCESS));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleWrongRow() {
        runDiff(createDt(r2), createDt(b2));

        verify(handler).endRow(captor.capture());
        assertEquals(WRONG, captor.getValue().getStatus());
    }

    private DataRow createRow(Integer... items) {
        return createDataRowBuilder(rowStructure).createRow(items);
    }

    private DataTable createDt(DataRow... rows) {
        return createDataTable(rowStructure, rows);
    }

    private DataTableDiff createDiff() {
        DataTableDiff diff = new DataTableDiff(rowStructure);
        diff.addListener(new DiffListenerAdapter(handler));
        return diff;
    }

}