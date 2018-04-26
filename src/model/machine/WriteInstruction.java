package model.machine;

import java.util.Optional;

import model.machine.exceptions.SuspendForWriteException;
import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.TouchMachineException;

/**
 * Define the instruction write
 * @author dorian
 */
public class WriteInstruction implements Instruction {

	/**
	 * Serial ID Version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Message associated to the instruction
	 */
	private String message;

	/**
	 * Create a new write instruction
	 * @param message Message associated
	 */
	public WriteInstruction(String message) {
		this.message = Optional.ofNullable(message).orElse("");
	}

	/**
	 * Execute the instruction
	 */
	@Override
	public int execute(int counter, IntegerStack stack) throws TouchMachineException {

		boolean ch = stack.pop() == 0;
		
		int num = stack.pop();
		StringBuilder builder = new StringBuilder(message);

		for (int i = 0; i < num; i++) {
			if(ch)
				builder.append(' ').append((char) stack.pop().intValue());
			else
				builder.append(' ').append(stack.pop());
		}
		
		throw new SuspendForWriteException("", builder.toString(), counter);
	}

	/**
	 * Convert instruction to string
	 */
	@Override
	public String toText(Code code) {
		return "print with message " + message;
	}

}
