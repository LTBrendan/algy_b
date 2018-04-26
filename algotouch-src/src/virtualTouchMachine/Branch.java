package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.UnknownAddressException;
import virtualTouchMachine.Branch.BRANCH_TYPE;

public class Branch implements Instruction {
	private static final long serialVersionUID = 8757878837588692080L;
	private Instruction branchInstruction = null;
	private BRANCH_TYPE branchType;

	public Branch(BRANCH_TYPE branchType, Instruction branchInstruction) {
		this.branchInstruction = branchInstruction;
		this.branchType = branchType;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) throws UnknownAddressException {
		boolean mustBranch = false;
		int nextInstruction = myInstructionCounter + 1;
		int operand;
		switch ($SWITCH_TABLE$virtualTouchMachine$Branch$BRANCH_TYPE()[this.branchType.ordinal()]) {
			case 1 :
				mustBranch = true;
				break;
			case 2 :
				operand = ((Integer) myStack.pop()).intValue();
				if (operand == 0) {
					mustBranch = true;
				}
				break;
			case 3 :
				operand = ((Integer) myStack.pop()).intValue();
				if (operand == 1) {
					mustBranch = true;
				}
		}

		if (mustBranch) {
			throw new UnknownAddressException(this.branchInstruction);
		} else {
			return nextInstruction;
		}
	}

	public String toText(Code code) {
		String result = "Branch ";
		switch ($SWITCH_TABLE$virtualTouchMachine$Branch$BRANCH_TYPE()[this.branchType.ordinal()]) {
			case 1 :
				result = result + "always : ";
				break;
			case 2 :
				result = result + "false :";
				break;
			case 3 :
				result = result + "true : ";
		}

		return result + code.indexOf(this.branchInstruction);
	}
}
