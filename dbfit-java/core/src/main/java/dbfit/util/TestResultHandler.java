package dbfit.util;

/**
 * This interface should be implemented
 * by the different test systems so that the
 * core of DbFit remains agnostic of Fit, Slim or
 * whatever else
 */
public interface TestResultHandler {
    void pass();
    void fail(String actualValue);
    void exception(Throwable e);
    void annotate(String message);
}
