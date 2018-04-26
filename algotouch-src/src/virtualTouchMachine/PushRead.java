package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.SuspendForReadException;

public class PushRead implements Instruction {
	private static final long serialVersionUID = -247402214685160627L;
	private String prompt;

	public PushRead(String prompt) {
		this.prompt = prompt;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) throws SuspendForReadException {
		throw new SuspendForReadException("INTEGER", this.prompt, myInstructionCounter);
	}

	public String toText(Code code) {
		return "PushRead : \"" + this.prompt + "\"";
	}
}
