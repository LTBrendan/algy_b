package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.OneOperandOperationType;

public class OneOperandOperation implements Instruction {
	private static final long serialVersionUID = -399431847398627946L;
	private OneOperandOperationType op;

	public OneOperandOperation(OneOperandOperationType op) {
		this.op = op;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) {
		int operand = ((Integer) myStack.pop()).intValue();
		int result = Integer.MAX_VALUE;
		switch ($SWITCH_TABLE$virtualTouchMachine$OneOperandOperationType()[this.op.ordinal()]) {
			case 1 :
				result = operand == 0 ? 1 : 0;
				break;
			case 2 :
				result = -operand;
		}

		myStack.push(Integer.valueOf(result));
		return myInstructionCounter + 1;
	}

	public String toText(Code code) {
		return "Op " + this.op;
	}
}
