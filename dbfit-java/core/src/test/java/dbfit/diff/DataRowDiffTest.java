package dbfit.diff;

import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.DiffListener;
import dbfit.util.DiffHandler;
import dbfit.util.DiffListenerAdapter;
import dbfit.util.DataCell;
import dbfit.util.DataRow;
import static dbfit.util.MatchStatus.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

public class DataRowDiffTest {

    @Mock private DiffHandler handler;

    private ArgumentCaptor<MatchResult> rowResultCaptor =
        ArgumentCaptor.forClass(MatchResult.class);

    private ArgumentCaptor<MatchResult> cellResultCaptor =
        ArgumentCaptor.forClass(MatchResult.class);

    private List<MatchResult> cellResults;
    private List<MatchResult> rowResults;

    private String[] columns = new String[] { "n", "2n" };

    private void runDiff(DataRow row1, DataRow row2) {
        runDiff(row1, row2, columns);
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
        HashMap<String, Object> rowValues = new HashMap<String, Object>();
        int i = 0;
        for (Integer item: items) {
            rowValues.put(columns[i++], item.toString());
        }
        return new DataRow(rowValues);
    }

}
