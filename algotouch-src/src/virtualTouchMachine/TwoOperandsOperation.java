package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.TwoOperandsOperationType;

public class TwoOperandsOperation implements Instruction {
	private static final long serialVersionUID = 1886094990870902690L;
	private TwoOperandsOperationType op;

	public TwoOperandsOperation(TwoOperandsOperationType op) {
		this.op = op;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) {
		int op2 = ((Integer) myStack.pop()).intValue();
		int op1 = ((Integer) myStack.pop()).intValue();
		int result = Integer.MAX_VALUE;
		switch ($SWITCH_TABLE$virtualTouchMachine$TwoOperandsOperationType()[this.op.ordinal()]) {
			case 1 :
				result = op1 + op2;
				break;
			case 2 :
				result = op1 - op2;
				break;
			case 3 :
				result = op1 * op2;
				break;
			case 4 :
				result = op1 / op2;
				break;
			case 5 :
				result = op1 % op2;
				break;
			case 6 :
				result = op1 == 1 && op2 == 1 ? 1 : 0;
				break;
			case 7 :
				result = op1 == 0 && op2 == 0 ? 0 : 1;
				break;
			case 8 :
				result = op1 == op2 ? 1 : 0;
				break;
			case 9 :
				result = op1 == op2 ? 0 : 1;
				break;
			case 10 :
				result = op1 < op2 ? 1 : 0;
				break;
			case 11 :
				result = op1 > op2 ? 1 : 0;
				break;
			case 12 :
				result = op1 <= op2 ? 1 : 0;
				break;
			case 13 :
				result = op1 >= op2 ? 1 : 0;
		}

		myStack.push(Integer.valueOf(result));
		return myInstructionCounter + 1;
	}

	public String toText(Code code) {
		return "TwoOp " + this.op;
	}
}
