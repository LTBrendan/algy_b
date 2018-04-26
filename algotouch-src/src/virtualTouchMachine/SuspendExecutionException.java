package virtualTouchMachine;

import virtualTouchMachine.TouchMachineException;

public class SuspendExecutionException extends TouchMachineException {
	private static final long serialVersionUID = 1819012808410084065L;
	private int suspendedAddress;
	private String todoMessage;
	private String todoStatement;

	public SuspendExecutionException(int suspendedAddress, String toDoMessage, String todoStatement) {
		super("Suspended because " + toDoMessage);
		this.suspendedAddress = suspendedAddress;
		this.todoMessage = toDoMessage;
		this.todoStatement = todoStatement;
	}

	public int getSuspendedAddress() {
		return this.suspendedAddress;
	}

	public String getToDoMessage() {
		return this.todoMessage;
	}

	public String getTodoStatement() {
		return this.todoStatement;
	}
}
