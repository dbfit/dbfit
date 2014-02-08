package dbfit.diff;

import dbfit.util.MatchResult;
import dbfit.util.DiffListener;
import dbfit.util.DiffHandler;
import dbfit.util.DiffListenerAdapter;
import dbfit.util.DataRow;
import dbfit.util.DataCell;
import static dbfit.util.MatchStatus.*;

import static dbfit.util.DiffTestUtils.createDataRowBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class DataRowDiffTest {

    @Mock private DiffHandler handler;
    @Mock private DataCellDiff childDiff;

    private ArgumentCaptor<MatchResult> rowResultCaptor = forClass(MatchResult.class);
    private ArgumentCaptor<MatchResult> cellResultCaptor = forClass(MatchResult.class);
    private ArgumentCaptor<MatchResult> resultCaptor = forClass(MatchResult.class);
    private ArgumentCaptor<DataCell> arg1Captor = forClass(DataCell.class);
    private ArgumentCaptor<DataCell> arg2Captor = forClass(DataCell.class);

    private List<MatchResult> cellResults;
    private List<MatchResult> rowResults;
    private List<MatchResult> allResults;

    private String[] columns = new String[] { "n", "2n" };

    private void runDiff(DataRow row1, DataRow row2) {
        runDiff(row1, row2, columns);
    }

    private void runUnadaptedDiff(DataRow row1, DataRow row2) {
        runUnadaptedDiff(row1, row2, columns);
    }

    @SuppressWarnings("unchecked")
    private void runDiff(DataRow row1, DataRow row2, String... colNames) {
        DataRowDiff diff = new DataRowDiff(colNames);
        diff.addListener(new DiffListenerAdapter(handler));

        diff.diff(row1, row2);

        verify(handler, times(colNames.length)).endCell(cellResultCaptor.capture());
        verify(handler).endRow(rowResultCaptor.capture());

        cellResults = cellResultCaptor.getAllValues();
        rowResults = rowResultCaptor.getAllValues();
    }

    private void runUnadaptedDiff(DataRow row1, DataRow row2, String... colNames) {
        DataRowDiff diff = new DataRowDiff(colNames);
        DiffListener listener = mock(DiffListener.class);
        diff.addListener(listener);

        diff.diff(row1, row2);

        verify(listener, times(1 + colNames.length)).onEvent(resultCaptor.capture());
        allResults = resultCaptor.getAllValues();
    }

    private void runWithMockedChildDiff(DataRow row1, DataRow row2) {
        DataRowDiff diff = new DataRowDiff(columns, childDiff);

        diff.diff(row1, row2);

        verify(childDiff, times(columns.length)).diff(
                arg1Captor.capture(), arg2Captor.capture());
    }

    @Test
    public void shouldInvokeDiffPerEachChildCell() {
        runWithMockedChildDiff(createRow(2, 4), createRow(3, 5));

        assertThat(arg1Captor.getAllValues(), hasItems(2, 4));
        assertThat(arg2Captor.getAllValues(), hasItems(3, 5));
    }

    @Test
    public void onExceptionShouldEmitExceptionEvent() {
        DataRowDiff diff = new DataRowDiff(columns, childDiff);
        DiffListener listener = mock(DiffListener.class);
        diff.addListener(listener);

        doThrow(new RuntimeException("Cruel!")).when(childDiff).diff(
                org.mockito.Matchers.any(DataCell.class),
                org.mockito.Matchers.any(DataCell.class));

        diff.diff(createRow(2, 4), createRow(5, 6));

        verify(listener).onEvent(resultCaptor.capture());
        assertThat(resultCaptor.getValue().getStatus(), is(EXCEPTION));
    }

    @Test
    public void numEventsShouldBeOnePerChildPlusSelf() {
        runUnadaptedDiff(createRow(2, 4), createRow(2, 4));
        assertEquals(3, allResults.size());
    }

    @Test
    public void shouldEmitChildrenDiffEventsOfTypeDataCell() {
        Class expectedType = DataCell.class;
        runUnadaptedDiff(createRow(2, 4), createRow(2, 4));

        assertThat(allResults.get(0).getType(), equalTo(expectedType));
        assertThat(allResults.get(1).getType(), equalTo(expectedType));
    }

    @Test
    public void shouldEmitDiffEventOfTypeDataRow() {
        Class expectedType = DataRow.class;
        runUnadaptedDiff(createRow(2, 4), createRow(2, 4));

        assertThat(allResults.get(2).getType(), equalTo(expectedType));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRowsWithMatchingAndMismatchingCells() {
        runDiff(createRow(2, 4), createRow(2, 44));

        assertThat(cellResults.get(0).getStatus(), is(SUCCESS));
        assertThat(cellResults.get(1).getStatus(), is(WRONG));
        assertThat(rowResults.get(0).getStatus(), is(WRONG));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRowsWithWrongCellsAndFinalOneSuccess() {
        runDiff(createRow(2, 4), createRow(3, 4));

        assertThat(cellResults.get(0).getStatus(), is(WRONG));
        assertThat(cellResults.get(1).getStatus(), is(SUCCESS));
        assertThat(rowResults.get(0).getStatus(), is(WRONG));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldIgnoreColumnsNotInList() {
        runDiff(createRow(2, 4), createRow(2, 44), "n");

        assertThat(cellResults.get(0).getStatus(), is(SUCCESS));
        assertThat(rowResults.get(0).getStatus(), is(SUCCESS));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatchingRows() {
        runDiff(createRow(2, 4), createRow(2, 4));

        assertThat(cellResults.get(0).getStatus(), is(SUCCESS));
        assertThat(cellResults.get(1).getStatus(), is(SUCCESS));
        assertThat(rowResults.get(0).getStatus(), is(SUCCESS));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMissingRow() {
        runDiff(createRow(2, 4), null);

        assertThat(cellResults.get(0).getStatus(), is(MISSING));
        assertThat(cellResults.get(1).getStatus(), is(MISSING));
        assertThat(rowResults.get(0).getStatus(), is(MISSING));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSurplusRow() {
        runDiff(null, createRow(2, 44));

        assertThat(cellResults.get(0).getStatus(), is(SURPLUS));
        assertThat(cellResults.get(1).getStatus(), is(SURPLUS));
        assertThat(rowResults.get(0).getStatus(), is(SURPLUS));
    }

    private DataRow createRow(int... items) {
        return createDataRowBuilder(columns).createRow(items);
    }

    public static <E> Matcher<Iterable<? extends E>> hasItems(int... values) {
        List<Matcher<? super E>> matchers = new ArrayList<>();
        for (int item : values) {
            matchers.add(hasToString(String.valueOf(item)));
        }

        return contains(matchers);
    }
}
