package virtualTouchMachine;

import virtualTouchMachine.TouchMachineException;

public class OutOfBoundsException extends TouchMachineException {
	private static final long serialVersionUID = 5186391518307653559L;

	public OutOfBoundsException(String string) {
		super(string);
	}
}
