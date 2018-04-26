package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.SuspendForReadException;

public class PushReadChar implements Instruction {
	private static final long serialVersionUID = 1L;
	private String prompt;

	public PushReadChar(String prompt) {
		this.prompt = prompt;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) throws SuspendForReadException {
		throw new SuspendForReadException("CHAR", this.prompt, myInstructionCounter);
	}

	public String toText(Code code) {
		return "PushReadChar : \"" + this.prompt + "\"";
	}
}
