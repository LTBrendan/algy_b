package virtualTouchMachine;

import java.io.Serializable;
import virtualTouchMachine.Code;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.TouchMachineException;

public interface Instruction extends Serializable {
	long serialVersionUID = 1059463550624489014L;

	int execute(int arg0, IntegerStack arg1) throws TouchMachineException;

	String toText(Code arg0);
}
