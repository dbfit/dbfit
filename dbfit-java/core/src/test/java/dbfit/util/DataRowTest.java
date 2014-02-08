package dbfit.util;

import dbfit.util.DiffTestUtils.DataRowBuilder;
import static dbfit.util.DiffTestUtils.createDataRowBuilder;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class DataRowTest {

    private final DataRowBuilder rb = createDataRowBuilder();
    private DataRow row = rb.createRow(2, 4);

    @Test
    public void testDataRowStringValueOfExistingColumns() {
        assertThat(row.getStringValue("c0"), is("2"));
        assertThat(row.getStringValue("c1"), is("4"));
    }

    @Test
    public void testDataRowStringValueOfNullCells() {
        row = rb.createRow(2, null);
        assertThat(row.getStringValue("c1"), is("null"));
    }

    @Test
    public void testDataRowStringValueOfMissingColumn() {
        assertThat(row.getStringValue("NO-SUCH-COLUMN"), is("null"));
    }

}
