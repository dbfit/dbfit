package dbfit.diff;

import dbfit.util.DataRow;
import dbfit.util.DataCell;
import dbfit.util.DiffListener;
import dbfit.util.MatchStatus;
import dbfit.util.MatchResult;
import static dbfit.util.MatchStatus.*;
import static dbfit.util.DataCell.createDataCell;

import java.util.List;
import java.util.ArrayList;

public class DataRowDiff {
    private List<DiffListener> listeners = new ArrayList<DiffListener>();
    private String[] columnNames;

    public DataRowDiff(final String[] columnNames) {
        this.columnNames = columnNames;
    }

    public void addListener(final DiffListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final DiffListener listener) {
        listeners.remove(listener);
    }

    public void diff(final DataRow dr1, final DataRow dr2) {
        MatchResult<DataRow, DataRow> rowResult =
                MatchResult.create(dr1, dr2, MatchStatus.SUCCESS);
        try {
            for (String column: columnNames) {
                createDataCellDiff(rowResult).diff(
                            createDataCell(dr1, column),
                            createDataCell(dr2, column));
            }
        } catch (Exception e) {
            rowResult.setException(e);
        } finally {
            notifyListeners(rowResult);
        }
    }

    private DataCellDiff createDataCellDiff(
            final MatchResult<DataRow, DataRow> rowResult) {
        DataCellDiff diff = new DataCellDiff();
        for (DiffListener lsnr: listeners) {
            diff.addListener(lsnr);
        }
        diff.addListener(createCellListener(rowResult));
        return diff;
    }

    private DiffListener createCellListener(
            final MatchResult<DataRow, DataRow> rowResult) {
        return new DiffListener() {
            @Override
            public void endRow(MatchResult<DataRow, DataRow> result) {
                // ignore
            }

            @Override
            public void endCell(MatchResult<DataCell, DataCell> result) {
                switch (result.getStatus()) {
                case WRONG:
                case SURPLUS:
                case MISSING:
                case EXCEPTION:
                    rowResult.setStatus(result.getStatus());
                    break;
                }
            }
        };
    }

    private void notifyListeners(final MatchResult<DataRow, DataRow> result) {
        for (DiffListener listener: listeners) {
            listener.endRow(result);
        }
    }

}
