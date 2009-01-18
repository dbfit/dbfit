package org.dbfit.greenpepper;
import com.greenpepper.Interpreter;
import com.greenpepper.Specification;
import com.greenpepper.Statistics;
import com.greenpepper.reflect.Fixture;

public class ExecuteInterpreter implements Interpreter{
	private Fixture f;
	private static final String[] EMPTY=new String[0];
	public ExecuteInterpreter(Fixture f){
		this.f=f;
	}
	@Override
	public void interpret(Specification specification) {
		Statistics s=new Statistics();
		specification.nextExample();
		try {
			f.send("execute").send(EMPTY);			
		}
		catch (Exception e){
			System.err.println(e);
			s.exception();		
		}
		specification.exampleDone(s);
	}
}
