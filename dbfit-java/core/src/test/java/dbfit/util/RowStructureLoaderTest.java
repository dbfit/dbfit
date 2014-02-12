package dbfit.util;

import fit.Parse;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

public class RowStructureLoaderTest {

    @Test
    public void shouldLoadFromParseColumnNames() {
        Parse row = createRowParse("col1", "col2?", "col3");
        RowStructure rs = RowStructureLoader.loadRowStructure(row);

        assertThat(rs.getColumnNames(), is(new String[] { "col1", "col2", "col3" }));
    }

    @Test
    public void shouldLoadFromParseKeyPropertiesNames() {
        Parse row = createRowParse("col1", "col2?", "col3");
        RowStructure rs = RowStructureLoader.loadRowStructure(row);

        assertThat(rs.getKeyProperties(), is(new boolean[] { true, false, true }));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRaiseExceptionOnEmptyColumnName() {
        Parse row = createRowParse("col1", "col2?", "");
        RowStructureLoader.loadRowStructure(row);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRaiseExceptionOnNullColumnName() {
        Parse row = createRowParse("col1", null, "col3");
        RowStructureLoader.loadRowStructure(row);
    }

    // Setup helpers
    private Parse createRowParse(final String... columns) {
        Parse row = new Parse("tr", null, null, null);
        Parse lastCell = row.parts;

        for (String col: columns) {
            Parse td = new Parse("td", col, null, null);

            if (lastCell == null) {
                lastCell = td;
                row.parts = lastCell;
            } else {
                lastCell.more = td;
                lastCell = lastCell.more;
            }
        }

        return row;
    }

}
