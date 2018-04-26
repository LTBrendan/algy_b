package model.machine;

import virtualTouchMachine.Array;
import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.TouchMachineException;

/**
 * Define the instrcution pusharray
 * @author dorian
 */
public class PushArray implements Instruction {

	/**
	 * Serial ID Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The array to push
	 */
	private Array array;

	/**
	 * Create a new instruction
	 * @param array array associated
	 */
	public PushArray(Array array) {
		this.array = array;
	}

	/**
	 * Execute the instrcution
	 */
	@Override
	public int execute(int address, IntegerStack stack) throws TouchMachineException {
		for (int i = array.getSize()-1; i >= 0; i--) {
			stack.push(array.getValue(i));
		}
		return address + 1;
	}

	/**
	 * Convert isntruction to string
	 */
	@Override
	public String toText(Code code) {
		return "pushArray " + array.getName();
	}

}
