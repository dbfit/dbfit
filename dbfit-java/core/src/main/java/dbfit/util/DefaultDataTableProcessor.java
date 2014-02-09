package dbfit.util;

public class DefaultDataTableProcessor implements DataTableProcessor {
    private final DataRowProcessor childProcessor;

    public DefaultDataTableProcessor(final DataRowProcessor childProcessor) {
        this.childProcessor = childProcessor;
    }

    @Override
    public void process(final DataTable table) {
        for (final DataRow row : table.getRows()) {
            childProcessor.process(row);
        }
    }
}
