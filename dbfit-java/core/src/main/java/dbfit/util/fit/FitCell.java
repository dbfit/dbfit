package dbfit.util.fit;

import dbfit.util.*;
import fit.Fixture;
import fit.Parse;
import fit.TypeAdapter;

public class FitCell extends Cell {
    private TestResultHandler testResultHandler;
    private ParseHelper parseHelper;

    public FitCell(DbParameterAccessor accessor, Parse fitCell, Fixture parentFixture) {
        super(fitCell.text(), accessor);

        Class<?> type = accessor.getJavaType();
        parseHelper = new ParseHelper(TypeAdapter.on(parentFixture, type));

        testResultHandler = new DbFitActionResultHandler(fitCell, parentFixture);
    }

    @Override
    public Object parse(String string) throws Exception {
        return parseHelper.parse(string);
    }

    @Override
    public TestResultHandler getTestResultHandler() {
        return testResultHandler;
    }
}
