package dbfit.util;

import fit.Parse;

public class RowStructureLoader {

    public static RowStructure loadRowStructure(final Parse headerRow) {
        String[] columnNames;
        boolean[] keyProperties;

        Parse headerCell = headerRow.parts;
        int colNum = headerRow.parts.size();
        columnNames = new String[colNum];
        keyProperties = new boolean[colNum];

        for (int i = 0; i < colNum; i++) {
            String currentName = headerCell.text();
            if (currentName == null) throw new UnsupportedOperationException("Column " + i + " does not have a name");
            currentName = currentName.trim();
            if (currentName.length() == 0)
                throw new UnsupportedOperationException("Column " + i + " does not have a name");
            columnNames[i] = NameNormaliser.normaliseName(currentName);
            keyProperties[i] = !currentName.endsWith("?");
            headerCell = headerCell.more;
        }

        return new RowStructure(columnNames, keyProperties);
    }
}
