package dbfit.diff;

import dbfit.util.MatchResult;
import dbfit.util.DiffListener;
import dbfit.util.DataRow;
import dbfit.util.DataCell;
import dbfit.util.MatchStatus;
import static dbfit.util.MatchStatus.*;

import static dbfit.util.DiffTestUtils.createDataRowBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class DataRowDiffTest {

    @Mock DataCellDiff childDiff;
    @Mock MatchResult<DataRow, DataRow> mockResult;
    @Mock DiffListener listener;

    @Captor ArgumentCaptor<MatchResult> resultCaptor;
    @Captor ArgumentCaptor<DataCell> arg1Captor;
    @Captor ArgumentCaptor<DataCell> arg2Captor;

    private List<MatchResult> allResults;

    private String[] columns = new String[] { "n", "2n" };

    private void runUnadaptedDiff(DataRow row1, DataRow row2) {
        runUnadaptedDiff(row1, row2, columns);
    }

    private void runUnadaptedDiff(DataRow row1, DataRow row2, String... colNames) {
        DataRowDiff diff = new DataRowDiff(colNames);
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
        Exception ex = new RuntimeException("Cruel World!");

        doThrow(ex).when(childDiff).diff(anyDataCell(), anyDataCell());

        diff.diff(mockResult);

        verify(mockResult).setException(ex);
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

    private DataRow createRow(Integer... items) {
        return createDataRowBuilder(columns).createRow(items);
    }

    public static <E> Matcher<Iterable<? extends E>> hasItems(Object... values) {
        List<Matcher<? super E>> matchers = new ArrayList<>();
        for (Object item : values) {
            matchers.add(hasToString(String.valueOf(item)));
        }

        return contains(matchers);
    }

    private DataCell anyDataCell() {
        return org.mockito.Matchers.any(DataCell.class);
    }
}
