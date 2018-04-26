package virtualTouchMachine;

import virtualTouchMachine.Array;
import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.OutOfBoundsException;

public class PushIndexedVar implements Instruction {
	private static final long serialVersionUID = 1965727074704083223L;
	Array myArray;

	public PushIndexedVar(Array myArray) {
		this.myArray = myArray;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) throws OutOfBoundsException {
		int index = ((Integer) myStack.pop()).intValue();
		if (index >= 0 && index < this.myArray.getSize()) {
			myStack.push(Integer.valueOf(this.myArray.getValue(index)));
			return myInstructionCounter + 1;
		} else {
			throw new OutOfBoundsException("Index out of bounds at line " + myInstructionCounter);
		}
	}

	public String toText(Code code) {
		return "PushIndexedVar " + this.myArray.getName();
	}
}
