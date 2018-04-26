package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.Variable;

public class PushVariable implements Instruction {
	private static final long serialVersionUID = 8498053754109150570L;
	private Variable v;

	public PushVariable(Variable v) {
		this.v = v;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) {
		myStack.push(Integer.valueOf(this.v.getValue()));
		return myInstructionCounter + 1;
	}

	public String toText(Code code) {
		return "PushVariable " + this.v.getName();
	}
}
