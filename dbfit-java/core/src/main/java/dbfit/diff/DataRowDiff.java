package dbfit.diff;

import dbfit.util.DataRow;
import dbfit.util.DataCell;
import dbfit.util.DiffListener;
import dbfit.util.NoOpDiffListenerAdapter;
import dbfit.util.MatchStatus;
import dbfit.util.MatchResult;
import dbfit.util.DiffResultsSummarizer;
import static dbfit.util.MatchStatus.*;
import static dbfit.util.DataCell.createDataCell;

import java.util.List;
import java.util.ArrayList;

public class DataRowDiff extends DiffBase {
    private String[] columnNames;

    public DataRowDiff(final String[] columnNames) {
        this.columnNames = columnNames;
    }

    public void diff(final DataRow dr1, final DataRow dr2) {
        DiffResultsSummarizer summer = createSummer(dr1, dr2);

        try {
            for (String column: columnNames) {
                createChildDiff(summer).diff(
                            createDataCell(dr1, column),
                            createDataCell(dr2, column));
            }
        } catch (Exception e) {
            summer.getResult().setException(e);
        } finally {
            notifyListeners(summer.getResult());
        }
    }

    private DiffResultsSummarizer createSummer(final DataRow dr1, final DataRow dr2) {
        return new DiffResultsSummarizer(
                MatchResult.create(dr1, dr2, DataRow.class),
                DataCell.class);
    }

    private DataCellDiff createChildDiff(final DiffResultsSummarizer summer) {
        DataCellDiff diff = new DataCellDiff();
        diff.addListeners(listeners);
        diff.addListener(summer);
        return diff;
    }
}
