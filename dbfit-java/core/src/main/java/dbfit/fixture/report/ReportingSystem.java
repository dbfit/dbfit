package dbfit.fixture.report;

import dbfit.util.MatchResult;

public interface ReportingSystem {
    public void addCell(MatchResult res);
    public void endRow(MatchResult res);
    public void endRow(MatchResult res, String description);
    public void addException(Throwable ex);
    public void incRight(); // increment right counts
}
