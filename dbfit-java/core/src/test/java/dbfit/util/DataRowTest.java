package dbfit.util;

import static dbfit.util.DiffTestUtils.createDataRowBuilder;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class DataRowTest {

    private DataRow row = createDataRowBuilder().createRow(2, 4);

    @Test
    public void testDataRowStringValueOfExistingColumns() {
        assertThat(row.getStringValue("c0"), is("2"));
        assertThat(row.getStringValue("c1"), is("4"));
    }

}
