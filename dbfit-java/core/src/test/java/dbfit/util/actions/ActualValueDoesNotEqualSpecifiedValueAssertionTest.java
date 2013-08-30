package dbfit.util.actions;

import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActualValueDoesNotEqualSpecifiedValueAssertionTest {
    @Test
    public void failsWhenActualEqualsSpecifiedInequality() throws Exception {
        TestCell cell = new TestCell("fail[abc]");
        cell.actual = "abc";

        action().run(cell, cell.resultHandler);

        verify(cell.resultHandler).annotate("= abc");
        verify(cell.resultHandler).fail("abc");
    }

    @Test
    public void passesWhenActualDoesNotEqualSpecifiedInequality() throws Exception {
        TestCell cell = new TestCell("fail[abc]");
        cell.actual = "def";

        action().run(cell, cell.resultHandler);

        verify(cell.resultHandler).annotate("= def");
        verify(cell.resultHandler).pass();
    }

    @Test
    public void appliesToCellsStaringWithFail() {
        DbParameterAccessor column = mock(DbParameterAccessor.class);
        when(column.getDirection()).thenReturn(Direction.OUTPUT);

        assertTrue(action().appliesTo(new TestCell("fail[abc]", column)));
        assertFalse(action().appliesTo(new TestCell("abc", column)));
    }

    private ActualValueDoesNotEqualSpecifiedValueAssertion action() {
        return new ActualValueDoesNotEqualSpecifiedValueAssertion();
    }
}
