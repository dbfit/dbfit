package dbfit.diff;

import dbfit.util.MatchResult;
import dbfit.util.DiffHandler;
import dbfit.util.DiffListenerAdapter;
import dbfit.util.DataRow;
import dbfit.util.MatchStatus;
import static dbfit.util.MatchStatus.*;

import static dbfit.util.DiffTestUtils.createDataRowBuilder;
import static dbfit.test.matchers.HasMatchStatus.*;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertThat;

import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;
import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
public class DataRowDiffParameterizedIntTest {
    public static final List<String> allColumns = asList("n", "2n");
    public static final String[] allColumnsArr = allColumns.toArray(new String[0]);

    private DataRowDiff diff;

    private DiffHandler handler;
    private ArgumentCaptor<MatchResult> rowResultCaptor = forClass(MatchResult.class);
    private ArgumentCaptor<MatchResult> cellResultCaptor = forClass(MatchResult.class);

    // parameterized tests arguments
    private DataRow row1;
    private DataRow row2;
    private List<String> colNames;
    private MatchStatus expectedRowStatus;
    private List<MatchStatus> expectedCellStatuses;

    public DataRowDiffParameterizedIntTest(
            List<List<Integer>> rows, List<String> colNames,
            MatchStatus expectedRowStatus, List<MatchStatus> expectedCellStatuses) {
        this.row1 = createDataRowBuilder(allColumnsArr).createRow(rows.get(0));
        this.row2 = createDataRowBuilder(allColumnsArr).createRow(rows.get(1));
        this.colNames = colNames;
        this.expectedRowStatus = expectedRowStatus;
        this.expectedCellStatuses = expectedCellStatuses;
    }

    @Before
    public void prepare() {
        diff = new DataRowDiff(colNames.toArray(new String[0]));
        handler = mock(DiffHandler.class);
        diff.addListener(new DiffListenerAdapter(handler));
    }

    @Parameters(name = "({index}): rows {0}/{1} -> expecting {2} -- {3}")
    public static Collection<Object[]> data() throws Exception {
        return java.util.Arrays.asList(new Object[][] {
            {asList(r(2, 4), r(2, 4)), allColumns, SUCCESS, asList(SUCCESS, SUCCESS)},
            {asList(r(2, 4), r(2, 5)), allColumns, WRONG,   asList(SUCCESS, WRONG)},
            {asList(r(2, 4), r(3, 4)), allColumns, WRONG,   asList(WRONG, SUCCESS)},
            {asList(r(2, 4),    null), allColumns, MISSING, asList(MISSING, MISSING)},
            {asList(   null, r(2, 4)), allColumns, SURPLUS, asList(SURPLUS, SURPLUS)},
            {asList(r(2, 4), r(2, 9)), cols("n"),  SUCCESS, asList(SUCCESS)},
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRowDiffStatus() {
        diff.diff(row1, row2);
        verify(handler).endRow(rowResultCaptor.capture());
        verifyResults(rowResultCaptor, asList(expectedRowStatus));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCellDiffStatuses() {
        diff.diff(row1, row2);
        verify(handler, times(colNames.size())).endCell(cellResultCaptor.capture());
        verifyResults(cellResultCaptor, expectedCellStatuses);
    }

    private void verifyResults(ArgumentCaptor<MatchResult> captor,
            List<MatchStatus> expectedStatuses) {
        assertThat(captor.getAllValues(), haveItemsStatuses(expectedStatuses));
    }

    private static List<Integer> r(Integer... items) {
        return asList(items);
    }

    private static List<String> cols(String... colNames) {
        return asList(colNames);
    }

}
