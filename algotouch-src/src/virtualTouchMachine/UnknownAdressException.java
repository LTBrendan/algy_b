package virtualTouchMachine;

import virtualTouchMachine.Instruction;
import virtualTouchMachine.TouchMachineException;

public class UnknownAddressException extends TouchMachineException {
	private static final long serialVersionUID = -4078130513927315220L;
	private Instruction branchInstruction;

	public UnknownAddressException(Instruction branchInstruction) {
		super("Unknown branch address");
		this.branchInstruction = branchInstruction;
	}

	public Instruction getBranchInstruction() {
		return this.branchInstruction;
	}
}
