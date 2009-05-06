package dbfit.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import fit.Fixture;
import fit.Parse;

/**
 * Convenience Helper class to provide reflection-based
 * method invocations of action methods taking String parameters.
 * I.e. a fitnesse table
 * <pre>
 * |a workflow method|parameterValue1|parameterValue2|
 * </pre>
 * would correspond to the method signature
 * <pre>
 * public void aWorkflowMethod(String parameter1, String parameter2)
 * </pre>
 */
public class WorkflowFixtureSupport extends Fixture {

    @SuppressWarnings("unchecked")
    @Override
    public void doCells(Parse cells) {
        String methodName = getMethodName(cells);
        List<String> params = getParams(cells);
        Class[] paramTypes = getParamTypes(params);
        try {
            Method action = getClass().getMethod(methodName, paramTypes);
            action.invoke(this, params.toArray());
        } catch (Exception e) {
            exception(cells, e);
            e.printStackTrace();
        }
    }

    protected String getMethodName(Parse cells) {
        return camel(cells.text());
    }

    protected List<String> getParams(Parse cells) {
        List<String> result = new ArrayList<String>();
        Parse curr = cells.more;
        while (curr != null) {
            result.add(curr.text());
            curr = curr.more;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected Class[] getParamTypes(List<String> params) {
        Class[] result = new Class[params.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = String.class;
        }
        return result;
    }
}
