package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.EndBlocException;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.TouchMachineException;

public class EndBloc implements Instruction {
	private static final long serialVersionUID = 7140622612907088692L;
	private String blockName;

	public EndBloc(String blocName) {
		this.blockName = blocName;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) throws TouchMachineException {
		throw new EndBlocException(this.blockName, myInstructionCounter);
	}

	public String toText(Code code) {
		return "EndBloc " + this.blockName;
	}
}
