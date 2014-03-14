package dbfit.environment;

import dbfit.util.Direction;
import static dbfit.util.Direction.*;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;

public class MySqlProcedureParametersParser {
    private Pattern returnPattern = Pattern.compile("(?<type>\\w+)" + "(\\W)|$");

    private Pattern parametersPattern = Pattern.compile(
        "((?<direction>\\w+)\\s+)?" +
        "(?<name>\\w+)" + "\\s+" +
        "(?<type>\\w+)" + "\\s*" +
        "(?<options>\\([^)]*\\))?\\s*(,|$)");

    public List<ParamDescriptor> parseParameters(String parametersExpression) {
        Matcher matcher = parametersPattern.matcher(
                parametersExpression.toLowerCase());
        ArrayList<ParamDescriptor> parameters = new ArrayList<>();

        while (matcher.find()) {
            parameters.add(parameterFrom(matcher));
        }

        return parameters;
    }

    public ParamDescriptor parseReturnType(String returnTypeExpression) {
        Matcher matcher = returnPattern.matcher(
                returnTypeExpression.toLowerCase());
        matcher.find();

        return resultFrom(matcher);
    }

    private ParamDescriptor parameterFrom(final Matcher m) {
        return new ParamDescriptor(
            m.group("name"),
            parseDirection(m.group("direction")),
            m.group("type"));
    }

    private ParamDescriptor resultFrom(final Matcher m) {
        return new ParamDescriptor("", RETURN_VALUE, m.group("type"));
    }

    private Direction parseDirection(final String direction) {
        switch(java.util.Objects.toString(direction, "in")) {
        case "in":
            return INPUT;
        case "inout":
            return INPUT_OUTPUT;
        case "out":
            return OUTPUT;
        default:
            throw new RuntimeException("Unknown direction: " + direction);
        }
    }
}
