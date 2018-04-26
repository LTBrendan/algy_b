package virtualTouchMachine;

import virtualTouchMachine.TouchMachineException;

public class EndBlocException extends TouchMachineException {
	private static final long serialVersionUID = -8790419313562708011L;
	private int suspendedAddress;

	public EndBlocException(String string, int suspendedAddress) {
		super(string);
		this.suspendedAddress = suspendedAddress;
	}

	public int getSuspendedAddress() {
		return this.suspendedAddress;
	}
}
