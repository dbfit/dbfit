package dbfit.util;

import dbfit.fixture.StatementExecution;
import static dbfit.util.Direction.INPUT_OUTPUT;
import org.junit.*;
import static org.mockito.Mockito.*;
import static java.sql.Types.*;

public class DbParameterAccessorTest {

    int sqlType = VARCHAR;
    String inputValue = "The input value";
    int position = 1;
    String userDefinedTypeName = "whatever";
    TypeTransformerFactory inputTransformerFactory;
    StatementExecution statement;
    DbParameterAccessor dbpa;

    @Before
    public void initialise() throws Exception {
        Class<?> javaType = String.class;
        inputTransformerFactory = mock(TypeTransformerFactory.class);
        statement = mock(StatementExecution.class);
        dbpa = new DbParameterAccessor("dummy", INPUT_OUTPUT, sqlType,
                                       userDefinedTypeName, javaType, position, inputTransformerFactory);
        dbpa.bindTo(statement, position);
    }

    @Test
    public void inputObjectIsTransformedBeforeJdbcBindingWhenTransformerSpecifierTest() throws Exception {
        TypeTransformer inputTransformer = mock(TypeTransformer.class);
        Integer outputValue = 5;
        when(inputTransformer.transform(inputValue)).thenReturn(outputValue);
        when(inputTransformerFactory.getTransformer(inputValue.getClass())).thenReturn(inputTransformer);
        dbpa.set(inputValue);
        verify(statement).setObject(position, outputValue, sqlType, userDefinedTypeName);
    }

    @Test
    public void inputObjectIsNotTransformedBeforeJdbcBindingWhenNoTransformerSpecifierTest() throws Exception {
        when(inputTransformerFactory.getTransformer(inputValue.getClass())).thenReturn(null);
        dbpa.set(inputValue);
        verify(statement).setObject(position, inputValue, sqlType, userDefinedTypeName);
    }
}
