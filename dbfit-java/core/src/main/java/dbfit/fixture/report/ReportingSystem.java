package dbfit.fixture.report;

public interface ReportingSystem {
    public void cellRight(String value);
    public void cellWrong(String actual, String expected);
    public void cellMissing(String expected);
    public void cellSurplus(String actual);
    public void cellException(String actual, String expected, Exception e);
}
