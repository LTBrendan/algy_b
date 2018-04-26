package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.TouchMachineException;

public class Continue implements Instruction {
	private static final long serialVersionUID = -5658370681009178620L;

	public int execute(int myInstructionCounter, IntegerStack myStack) throws TouchMachineException {
		return myInstructionCounter + 1;
	}

	public String toText(Code code) {
		return "Continue ";
	}
}
