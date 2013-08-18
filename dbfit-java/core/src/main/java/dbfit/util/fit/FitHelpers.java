package dbfit.util.fit;

import fit.Parse;

import java.util.ArrayList;
import java.util.List;

public class FitHelpers {
    public static List<String> getCellTextFrom(Parse cells) {
        List<String> cellText = new ArrayList<String>();
        for (; cells != null; cells = cells.more) {
            cellText.add(cells.text());
        }
        return cellText;
    }

    public static List<Parse> asCellList(Parse row) {
        List<Parse> cells = new ArrayList<Parse>();
        for (Parse cell = row.parts; cell != null; cell = cell.more) {
            cells.add(cell);
        }
        return cells;
    }
}
