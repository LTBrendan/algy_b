package virtualTouchMachine;

import virtualTouchMachine.Variable;

public class Var implements Variable {
	int value;
	String name;

	public Var(String name) {
		this.name = name;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getName() {
		return this.name;
	}
}
