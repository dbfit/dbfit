package org.dbfit.greenpepper;

import org.dbfit.greenpepper.util.GreenPepperTestHost;

public class Parameter {
	private String name;
	private String value;
	public Parameter(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	} 
	public String getValue() {
		return value;
	}
	public void set(){
		GreenPepperTestHost.getInstance().setSymbolValue(name, value);
	}
}
