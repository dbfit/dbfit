package dbfit.util;

import static dbfit.util.MatchStatus.*;

import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import static java.util.Arrays.asList;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class DiffResultsSummarizerTest {

    private static final Class<?> CHILD_TYPE = DataCell.class;

    private MatchResult result;
    private DiffResultsSummarizer summer;

    // parameterized tests args
    private final MatchStatus expectedStatus;
    private final List<MatchStatus> childrenEvents;
    private final String expectedException;

    public DiffResultsSummarizerTest(MatchStatus expectedStatus,
            List<MatchStatus> childrenEvents, String expectedException) {
        this.expectedStatus = expectedStatus;
        this.childrenEvents = childrenEvents;
        this.expectedException = expectedException;
    }

    @Before
    public void prepare() {
        DataRow obj1 = (expectedStatus == SURPLUS) ? null : mock(DataRow.class);
        DataRow obj2 = (expectedStatus == MISSING) ? null : mock(DataRow.class);
        result = MatchResult.create(obj1, obj2, DataRow.class);
        summer = new DiffResultsSummarizer(result, CHILD_TYPE);
    }

    @Parameters(name = "({index}): events {1} -> expecting {0}")
    public static Collection<Object[]> data() throws Exception {
        return java.util.Arrays.asList(new Object[][] {
            {SUCCESS,   asList(SUCCESS, SUCCESS, SUCCESS), null},
            {SUCCESS,   Arrays.<MatchStatus>asList(), null},
            {WRONG,     asList(SUCCESS, WRONG, SUCCESS), null},
            {EXCEPTION, asList(EXCEPTION, WRONG, SUCCESS), "0"},
            {EXCEPTION, asList(SUCCESS, EXCEPTION, SUCCESS, EXCEPTION), "1"},
            {EXCEPTION, asList(WRONG, EXCEPTION), "1"},
            {WRONG,     asList(SUCCESS, MISSING, SUCCESS), null},
            {WRONG,     asList(SUCCESS, SURPLUS, SUCCESS), null},
            {MISSING,   asList(SUCCESS, EXCEPTION, SUCCESS), null},
            {SURPLUS,   asList(SUCCESS, WRONG), null},
        });
    }

    @Test
    public void shouldProduceCorrectSummaryResult() {
        int i = 0;

        for (MatchStatus childStatus: childrenEvents) {
            summer.onEvent(createChildResult(childStatus, i++));
        }

        assertThat(summer.getStatus(), is(expectedStatus));

        if (expectedStatus == EXCEPTION) {
            assertThat(result.getException().getMessage(), is(expectedException));
        }
    }

    private static MatchResult createChildResult(MatchStatus status, int childNo) {
        MatchResult res = MatchResult.create(
                Mockito.mock(CHILD_TYPE),
                Mockito.mock(CHILD_TYPE), status, CHILD_TYPE);

        if (status == EXCEPTION) {
            res.setException(new Exception(Integer.toString(childNo)));
        }

        return res;
    }

}
