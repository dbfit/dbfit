package dbfit.util;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.List;
import static java.util.Arrays.asList;

@RunWith(MockitoJUnitRunner.class)
public class MatchableDataTableTest {

    @Mock private DataRow r1;
    @Mock private DataRow r2;
    @Mock private DataRow r3;
    @Mock private DataRow r4;
    @Mock private DataTable mockedDataTable;

    @Mock private Map<String,Object> matchingProperties;

    private List<DataRow> rows;
    private MatchableDataTable mdt;

    @Before
    public void prepare() {
        when(r3.matches(matchingProperties)).thenReturn(true);

        rows = asList(r1, r2, r3, r4);
        when(mockedDataTable.getRows()).thenReturn(rows);
        mdt = new MatchableDataTable(mockedDataTable);
    }

    @Test
    public void initiallyAllRowsShouldBeUnprocessed() {
        assertEquals(mdt.getUnprocessedRows(), rows);
    }

    @Test
    public void markShouldRemoveItFromUnprocessedR1() {
        mdt.markProcessed(r1);
        assertEquals(asList(r2, r3, r4), mdt.getUnprocessedRows());
    }

    @Test
    public void markShouldRemoveItFromUnprocessedR2() {
        mdt.markProcessed(r2);
        assertEquals(asList(r1, r3, r4), mdt.getUnprocessedRows());
    }

    @Test
    public void markWithNullShouldMakeNoChange() {
        final int ORIGINAL_SIZE = mdt.getUnprocessedRows().size();
        mdt.markProcessed(null);
        assertEquals(ORIGINAL_SIZE, mdt.getUnprocessedRows().size());
    }

    @Test
    public void findFirstUnprocessedRowShouldBeR1() throws NoMatchingRowFoundException {
        DataRow firstUnprocessed = mdt.findFirstUnprocessedRow();
        assertEquals(r1, firstUnprocessed);
    }

    @Test
    public void findFirstUnprocessedRowShouldBeR2() throws NoMatchingRowFoundException {
        mdt.markProcessed(r1);

        DataRow firstUnprocessed = mdt.findFirstUnprocessedRow();
        assertEquals(r2, firstUnprocessed);
    }

    @Test
    public void findMatchingShouldBeR3() throws NoMatchingRowFoundException {
        DataRow matchingRow = mdt.findMatching(matchingProperties);

        assertEquals(r3, matchingRow);
    }

    @Test(expected = NoMatchingRowFoundException.class)
    public void findMatchingOfProcessedRowShouldRaiseException() throws NoMatchingRowFoundException {
        mdt.markProcessed(r3);

        DataRow matchingRow = mdt.findMatching(matchingProperties);
    }

    @Test
    public void findMatchingNoThrowShouldReturnNullOnMiss() {
        mdt.markProcessed(r3);
        assertNull(mdt.findMatchingNothrow(matchingProperties));
    }

    @Test
    public void shouldDelegateGetColumnsToDataTable() {
        mdt.getColumns();

        verify(mockedDataTable).getColumns();
    }
}

