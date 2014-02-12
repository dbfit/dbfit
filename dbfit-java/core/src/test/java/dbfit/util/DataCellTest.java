package dbfit.util;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataCellTest {

    @Mock private DataRow row;
    @Mock private DataRow row2;
    private DataCell cell;

    @Before
    public void prepare() {
        when(row.getStringValue("n")).thenReturn("2");
        when(row.getStringValue("2n")).thenReturn("4");

        when(row2.getStringValue("n")).thenReturn("1");
        when(row2.getStringValue("2n")).thenReturn("2");

        cell = new DataCell(row, "n");
    }

    @Test
    public void toStringShouldInvokeDataRowGetStringValue() {
        cell.toString();
        verify(row).getStringValue("n");
    }

    @Test
    public void toStringShouldReturnCorrectValue() {
        assertThat(cell.toString(), is("2"));
    }

    @Test
    public void shouldBeEqualToCellWithIdenticalContent() {
        DataCell cell2 = new DataCell(row2, "2n");
        assertTrue(cell.equalTo(cell2));
    }

    @Test
    public void shouldNotBeEqualToCellWithDifferentContent() {
        DataCell cell2 = new DataCell(row2, "n");
        assertFalse(cell.equalTo(cell2));
    }

    @Test
    public void shouldNotBeEqualToNull() {
        assertFalse(cell.equalTo(null));
    }

    @Test
    public void createDataCellOfNullRowShouldReturnNull() {
        assertNull(DataCell.createDataCell(null, "n"));
    }

    @Test
    public void createDataCellOfValidRowShouldInstantiate() {
        DataCell cell2 = DataCell.createDataCell(row2, "2n");
        assertNotNull(cell2);
        assertTrue(cell.equalTo(cell2));
    }

}
