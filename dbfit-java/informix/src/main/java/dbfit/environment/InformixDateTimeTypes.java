package dbfit.environment;

import java.util.List;
import java.util.Arrays;

class InformixDateTimeTypes {
    public static final List<String> TIME_TYPES = Arrays.asList(
            "DATETIME HOUR TO HOUR",
            "DATETIME HOUR TO MINUTE",
            "DATETIME HOUR TO SECOND",
            "DATETIME MINUTE TO MINUTE",
            "DATETIME MINUTE TO SECOND",
            "DATETIME SECOND TO SECOND");

    public static final List<String> TIMESTAMP_TYPES = Arrays.asList(
            "DATETIME YEAR TO YEAR",
            "DATETIME YEAR TO MONTH",
            "DATETIME YEAR TO DAY",
            "DATETIME YEAR TO HOUR",
            "DATETIME YEAR TO MINUTE",
            "DATETIME YEAR TO SECOND",
            "DATETIME YEAR TO FRACTION(1)",
            "DATETIME YEAR TO FRACTION(2)",
            "DATETIME YEAR TO FRACTION(3)",
            "DATETIME YEAR TO FRACTION(4)",
            "DATETIME YEAR TO FRACTION(5)",
            "DATETIME MONTH TO MONTH",
            "DATETIME MONTH TO DAY",
            "DATETIME MONTH TO HOUR",
            "DATETIME MONTH TO MINUTE",
            "DATETIME MONTH TO SECOND",
            "DATETIME MONTH TO FRACTION(1)",
            "DATETIME MONTH TO FRACTION(2)",
            "DATETIME MONTH TO FRACTION(3)",
            "DATETIME MONTH TO FRACTION(4)",
            "DATETIME MONTH TO FRACTION(5)",
            "DATETIME DAY TO DAY",
            "DATETIME DAY TO HOUR",
            "DATETIME DAY TO MINUTE",
            "DATETIME DAY TO SECOND",
            "DATETIME DAY TO FRACTION(1)",
            "DATETIME DAY TO FRACTION(2)",
            "DATETIME DAY TO FRACTION(3)",
            "DATETIME DAY TO FRACTION(4)",
            "DATETIME DAY TO FRACTION(5)",
            "DATETIME HOUR TO FRACTION(1)",
            "DATETIME HOUR TO FRACTION(2)",
            "DATETIME HOUR TO FRACTION(3)",
            "DATETIME HOUR TO FRACTION(4)",
            "DATETIME HOUR TO FRACTION(5)",
            "DATETIME MINUTE TO FRACTION(1)",
            "DATETIME MINUTE TO FRACTION(2)",
            "DATETIME MINUTE TO FRACTION(3)",
            "DATETIME MINUTE TO FRACTION(4)",
            "DATETIME MINUTE TO FRACTION(5)",
            "DATETIME SECOND TO FRACTION(1)",
            "DATETIME SECOND TO FRACTION(2)",
            "DATETIME SECOND TO FRACTION(3)",
            "DATETIME SECOND TO FRACTION(4)",
            "DATETIME SECOND TO FRACTION(5)");
}
