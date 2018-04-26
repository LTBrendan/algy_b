package virtualTouchMachine;

import virtualTouchMachine.CallMacroException;
import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.TouchMachineException;

public class CallMacroCode implements Instruction {
	private static final long serialVersionUID = -3661366510931576315L;
	private Code codeToExecute;

	public CallMacroCode(Code codeToExecute) {
		this.codeToExecute = codeToExecute;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) throws TouchMachineException {
		int returnAddress = myInstructionCounter + 1;
		throw new CallMacroException("Call macro", this.codeToExecute, returnAddress);
	}

	public String toText(Code code) {
		return "Call macro " + this.codeToExecute.getName();
	}
}
