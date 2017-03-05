package dbfit.util;

import fit.Fixture;
import fit.Parse;

class CellHelper {

    static void appendObjectValue(Parse cell, Object value) {
        cell.addToBody(Fixture.gray("= " + String.valueOf(value)));
    }

    static void appendObjectValue(Parse cell, Object value, boolean isVisible) {
        if (isVisible) {
            appendObjectValue(cell, value);
        }
    }
}
