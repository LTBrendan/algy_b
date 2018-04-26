package model;

import virtualTouchMachine.Code;

/**
 * Simple interface that allows to create specific lambda
 * @author Daphnis
 *
 */
@FunctionalInterface
public interface Convertor {

	/**
	 * Convert all the argument into an instruction and update the program
	 * @param prog the program currently being constructed
	 * @param code (can be null), the code associated if a macro is being processed
	 * @param arg the arguments
	 */
	public void apply(Program prog, Code code, String arg);
	
}
