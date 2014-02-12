package dbfit.util;

import fit.Parse;

public class RowStructureLoader {

    public static RowStructure loadRowStructure(final Parse headerRow) {
        Parse headerCell = headerRow.parts;
        int colNum = headerRow.parts.size();
        String[] columnNames = new String[colNum];
        boolean[] keyProperties = new boolean[colNum];

        for (int i = 0; i < colNum; i++) {
            loadColumnStructure(headerCell, i, columnNames, keyProperties);
            headerCell = headerCell.more;
        }

        return new RowStructure(columnNames, keyProperties);
    }

    private static void loadColumnStructure(final Parse cell, int index,
                final String[] columnNames, final boolean[] keyProperties) {
        String name = getCellText(cell, index);
        columnNames[index] = NameNormaliser.normaliseName(name);
        keyProperties[index] = !name.endsWith("?");
    }

    private static String getCellText(final Parse cell, int index) {
        if ( (cell.body == null) || cell.text().isEmpty() ) {
            throw new UnsupportedOperationException(
                    "Column " + index + " does not have a name");
        } else {
            return cell.text();
        }
    }

}
