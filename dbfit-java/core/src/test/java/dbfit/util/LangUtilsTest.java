package dbfit.util;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class LangUtilsTest {
    @Test public void repeat() {
        assertEquals(asList("A","A","A"), LangUtils.repeat("A", 3));
    }

    @Test public void join() {
        assertEquals("A,B,C", LangUtils.join(asList("A", "B", "C"), ","));
    }

    @Test public void enquoteAndJoin() {
        assertEquals("[A].[B].[C]", LangUtils.enquoteAndJoin(asList("A", "B", "C"), ".", "[", "]"));
    }
}
