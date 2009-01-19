package org.dbfit.greenpepper;

import dbfit.util.Options;

public class Option {
	private String name;
	private String value;
	public Option(String name, String value) {
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
		Options.setOption(name, value);
	}
}
