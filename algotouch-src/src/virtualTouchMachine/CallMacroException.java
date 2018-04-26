
package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.TouchMachineException;

public class CallMacroException extends TouchMachineException {
	private static final long serialVersionUID = -7197717483853954531L;
	private Code codeToExecute;
	private int returnAddress;

	public CallMacroException(String string, Code codeToExecute, int returnAddress) {
		super(string);
		this.codeToExecute = codeToExecute;
		this.returnAddress = returnAddress;
	}

	public Code getCodeToExecute() {
		return this.codeToExecute;
	}

	public int getReturnAddress() {
		return this.returnAddress;
	}
}
