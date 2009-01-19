package org.dbfit.greenpepper;

import com.greenpepper.Interpreter;
import com.greenpepper.Specification;
import com.greenpepper.Statistics;
import com.greenpepper.reflect.Fixture;

/** set option or parameter */
public class SetInterpreter implements Interpreter{
	private Fixture f;
	private static final String[] EMPTY=new String[0];
	
	public SetInterpreter(Fixture f){
		this.f=f;
	}
	@Override
	public void interpret(Specification specification) {
		Statistics s=new Statistics();
		specification.nextExample();
		try {
			f.send("set()").send(EMPTY);			
		}
		catch (Exception e){
			System.err.println(e);
			s.exception();		
		}
		specification.exampleDone(s);
	}

}
