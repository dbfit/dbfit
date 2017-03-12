package dbfit.util;

import fit.Binding;
import fit.Fixture;
import fit.Parse;

import static dbfit.util.SymbolUtil.isSymbolGetter;
import static dbfit.util.SymbolUtil.isSymbolHidden;
import static dbfit.util.CellHelper.appendObjectValue;

public class SymbolAccessSetBinding extends Binding.SetBinding {

    @Override
    public void doCell(Fixture fixture, Parse cell) throws Throwable {
        String text = cell.text();
        if (isSymbolGetter(text)) {
            Object value = dbfit.util.SymbolUtil.getSymbol(text);
            appendObjectValue(cell, value, !isSymbolHidden(text));
            adapter.set(value);
            return;
        }
        super.doCell(fixture, cell);
    }

}
