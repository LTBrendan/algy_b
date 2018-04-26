package model.machine.exceptions;

import virtualTouchMachine.TouchMachineException;

/**
 * Exception launched when program need to write a variable on the screen
 * @author dorian
 */
public class SuspendForWriteException extends TouchMachineException{

	/**
	 * Serial ID Version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Message associated to the instruction
	 */
	private String message;
	
	/**
	 * Address to restart the program
	 */
	private int returnAddress;
	
	/**
	 * Create a new Exception to write a variable on the screen
	 * @param string Message of exception
	 * @param message Message to display
	 * @param returnAddress Address to restart execution
	 */
	public SuspendForWriteException(String string, String message, int returnAddress) {
		super(string);
		this.message = message;
		this.returnAddress = returnAddress;
	}

	/**
	 * Get the message
	 * @return The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Get the return address
	 * @return the address
	 */
	public int getReturnAddress() {
		return returnAddress;
	}
}
