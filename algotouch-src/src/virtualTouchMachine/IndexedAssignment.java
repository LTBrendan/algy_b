package virtualTouchMachine;

import virtualTouchMachine.Array;
import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.OutOfBoundsException;

public class IndexedAssignment implements Instruction {
	private static final long serialVersionUID = -1599241955767752956L;
	private static final String INDEX_OUT_OF_BOUNDS = "Index out of bounds";
	protected Array myArray;

	public IndexedAssignment(Array myArray) {
		this.myArray = myArray;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) throws OutOfBoundsException {
		int index = ((Integer) myStack.pop()).intValue();
		int value = ((Integer) myStack.pop()).intValue();
		if (index >= 0 && index < this.myArray.getSize()) {
			this.myArray.setValue(index, value);
			return myInstructionCounter + 1;
		} else {
			throw new OutOfBoundsException("Index out of bounds");
		}
	}

	public String toText(Code code) {
		return "IndexedAssignement " + this.myArray.getName();
	}
}
