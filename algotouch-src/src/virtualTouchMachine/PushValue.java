package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;

public class PushValue implements Instruction {
	private static final long serialVersionUID = -5192317914391418460L;
	private int value;

	public PushValue(int i) {
		this.value = i;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) {
		myStack.push(Integer.valueOf(this.value));
		return myInstructionCounter + 1;
	}

	public String toText(Code code) {
		return "PushValue " + this.value;
	}
}
