package dbfit.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContentOfTableCellTest {
    @Test public void canExtractInequalityValue() {
        assertEquals("abc", new ContentOfTableCell("fail[abc]").getExpectedFailureValue());
    }
}
