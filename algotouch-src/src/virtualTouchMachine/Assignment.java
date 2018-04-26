package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.Variable;

public class Assignment implements Instruction {
	private static final long serialVersionUID = -6444033563495848552L;
	private Variable v;

	public Assignment(Variable v) {
		this.v = v;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) {
		this.v.setValue(((Integer) myStack.pop()).intValue());
		return myInstructionCounter + 1;
	}

	public String toText(Code code) {
		return "Assign " + this.v.getName();
	}
}
