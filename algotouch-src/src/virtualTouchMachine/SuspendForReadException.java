package virtualTouchMachine;

import virtualTouchMachine.TouchMachineException;

public class SuspendForReadException extends TouchMachineException {
	private static final long serialVersionUID = 396465528858851476L;
	private int suspendedAddress;
	private String prompt;

	public SuspendForReadException(String string, String prompt, int suspendedAddress) {
		super(string);
		this.suspendedAddress = suspendedAddress;
		this.prompt = prompt;
	}

	public int getSuspendedAddress() {
		return this.suspendedAddress;
	}

	public String getPrompt() {
		return this.prompt;
	}
}
