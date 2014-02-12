package dbfit.util;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

public class MatchingMaskBuilderTest {

    private dbfit.util.RowStructure rowStructure = new RowStructure(
            new String[] { "n", "2n" }, /* names */
            new boolean[] { true, false } /* keys */
        );

    private Map<String, Object> mask;

    @Before
    public void prepare() {
        DataRow row = mock(DataRow.class);

        when(row.get("n")).thenReturn("2");
        when(row.get("2n")).thenReturn("4");

        mask = new MatchingMaskBuilder(rowStructure).buildMatchingMask(row);
    }

    @Test
    public void matchingMaskSizeShouldBeCountOfKeyColumns() {
        assertEquals(1, mask.size());
    }

    @Test
    public void maskShouldMapToRowValueOnKeyColumnLookup() {
        assertEquals("2", mask.get("n"));
    }

    @Test
    public void maskShouldNotContainNonKeyColumns() {
        assertThat(mask, not(hasKey("2n")));
    }

    @Test
    public void maskShouldNotContainNonExistentColumns() {
        assertThat(mask, not(hasKey("non-existent-column")));
    }

}
