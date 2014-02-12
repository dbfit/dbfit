package dbfit.util;

import java.util.Map;
import java.util.HashMap;

public class MatchingMaskBuilder {
    private RowStructure rowStructure;

    public MatchingMaskBuilder(final RowStructure rowStructure) {
        this.rowStructure = rowStructure;
    }

    public Map<String, Object> buildMatchingMask(final DataRow dr) {
        final Map<String, Object> matchingMask = new HashMap<String, Object>();
        for (int i = 0; i < rowStructure.size(); i++) {
            addToMask(i, matchingMask, dr);
        }

        return matchingMask;
    }

    private void addToMask(int index, final Map<String, Object> mask, DataRow dr) {
        if (rowStructure.isKeyColumn(index)) {
            String columnName = rowStructure.getColumnName(index);
            mask.put(columnName, dr.get(columnName));
        }
    }

}
