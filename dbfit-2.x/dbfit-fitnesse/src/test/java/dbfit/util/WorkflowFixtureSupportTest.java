package dbfit.util;

import java.util.StringTokenizer;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import fit.Parse;
import fit.exception.FitParseException;


public class WorkflowFixtureSupportTest extends WorkflowFixtureSupport {

    boolean methodWithZeroArgumentsCalled;
    boolean methodWithOneArgumentCalled;
    boolean methodWithTwoArgumentsCalled;
    String parameterOne;
    String parameterTwo;
    
    @Before
    public void setUp() {
        methodWithZeroArgumentsCalled = false;
        methodWithOneArgumentCalled = false;
        methodWithTwoArgumentsCalled = false;
        parameterOne = null;
        parameterTwo = null;
    }
    
    @Test
    public void testMethodWithZeroArgumentsDispatched() throws FitParseException {
        Parse cells = constructParse("actionMethod");
        doCells(cells);
        Assert.assertTrue("methodWithZeroArguments should have been called", methodWithZeroArgumentsCalled);
        Assert.assertFalse(cells.tag.contains("error"));
    }
    
    @Test
    public void testMethodWithOneArgumentDispatched() throws FitParseException {
        Parse cells = constructParse("actionMethod|argument1");
        doCells(cells);
        Assert.assertTrue("methodWithOneArgument should have been called", methodWithOneArgumentCalled);
        Assert.assertFalse("methodWithZeroArguments should not have been called", methodWithZeroArgumentsCalled);
        Assert.assertEquals("argument1", parameterOne);
        Assert.assertFalse(cells.tag.contains("error"));
    }
    
    @Test
    public void testMethodWithTwoArgumentsDispatched() throws FitParseException {
        Parse cells = constructParse("actionMethod|argument1|argument2");
        doCells(cells);
        Assert.assertTrue("methodWithTwoArguments should have been called", methodWithTwoArgumentsCalled);
        Assert.assertEquals("argument1", parameterOne);
        Assert.assertEquals("argument2", parameterTwo);
        Assert.assertFalse(cells.tag.contains("error"));
    }
    
    @Test
    public void testMissingMethodShowsException() throws FitParseException {
        Parse cells = constructParse("missingMethod|argument1");
        doCells(cells);
        Assert.assertTrue(cells.tag.contains("error"));
        Assert.assertTrue(cells.body.contains("java.lang.NoSuchMethodException"));
    }
    
    @Test
    public void testWrongParameterCountShowsException() throws FitParseException {
        Parse cells = constructParse("actionMethod|argument1|argument2|surplusArgument");
        doCells(cells);
        Assert.assertTrue(cells.tag.contains("error"));
        Assert.assertTrue(cells.body.contains("java.lang.NoSuchMethodException"));
    }
    
    private Parse constructParse(String cells) throws FitParseException {
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(cells, "|");
        sb.append("<table><tr>");
        while (st.hasMoreTokens()) {
            sb.append("<td>");
            sb.append(st.nextToken());
            sb.append("</td>");
        }
        sb.append("</tr></table>");
        Parse p = new Parse(sb.toString());
        return p.parts.parts;
    }
    
    public void actionMethod() {
        methodWithZeroArgumentsCalled = true;
    }

    public void actionMethod(String arg) {
        methodWithOneArgumentCalled = true;
        parameterOne = arg;
    }

    public void actionMethod(String arg1, String arg2) {
        methodWithTwoArgumentsCalled = true;
        parameterOne = arg1;
        parameterTwo = arg2;
    }
}
