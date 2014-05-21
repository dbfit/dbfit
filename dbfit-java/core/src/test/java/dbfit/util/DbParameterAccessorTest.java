package dbfit.util;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;

import dbfit.fixture.StatementExecution;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DbParameterAccessorTest {

	@Mock StatementExecution stex;
	DbParameterAccessor paInput;
	DbParameterAccessor paOutput;
	
	@Before
	public void setUp() throws Exception {
		paInput = new DbParameterAccessor("paInput", Direction.INPUT, 123, String.class, 1);
		paOutput = new DbParameterAccessor("paOutput", Direction.OUTPUT, 42, String.class, 2);
	}

	@Test
	public void bindToWithInputAndSingleIndexShouldNotCauseImmediateBinding() throws SQLException {
		paInput.bindTo(stex, 1);
		verifyZeroInteractions(stex);
	}

	@Test
	public void bindToWithOutputAndSingleIndexShouldCauseImmediateBinding() throws SQLException {
		paOutput.bindTo(stex, 10);
		verify(stex).registerOutParameter(10, 42);
	}

	@Test
	public void setFollowingSingleInputBindToShouldCauseSingleBind() throws Exception {
		paInput.bindTo(stex, 10);
		paInput.set("Hello");
		verify(stex).setObject(10, "Hello");
	}

	@Test
	public void getFollowingSingleOutputBindShouldRetrieveObject() throws Exception {
		when(stex.getObject(10)).thenReturn("The Answer");
		
		paOutput.bindTo(stex, 10);
		assertEquals("The Answer", paOutput.get());
		
		verify(stex).registerOutParameter(10, 42);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void bindToWithOutputAndMultipleIndicesShouldThrowException() throws SQLException {
		paOutput.bindTo(stex, 10, 11);
	}
	
	@Test
	public void setFollowingInputBindToWithMultipleIndicesShouldCauseMultipleBinds() throws Exception {
		paInput.bindTo(stex, 10, 11);
		paInput.set("Hello");
		verify(stex).setObject(10, "Hello");
		verify(stex).setObject(11, "Hello");
	}
}
