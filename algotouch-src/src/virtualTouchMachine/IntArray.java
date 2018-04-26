package virtualTouchMachine;

import virtualTouchMachine.Array;

public class IntArray implements Array {
	protected String name;
	protected int[] myArray;
	protected int size;

	public IntArray(String name, int size) {
		this.myArray = new int[size];
		this.name = name;
		this.size = size;
	}

	public int getValue(int index) {
		return this.myArray[index];
	}

	public void setValue(int index, int value) {
		this.myArray[index] = value;
	}

	public int getSize() {
		return this.size;
	}

	public String getName() {
		return this.name;
	}
}
