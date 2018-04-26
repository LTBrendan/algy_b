package virtualTouchMachine;

import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.SuspendExecutionException;

public class Todo implements Instruction {
	private static final long serialVersionUID = -8880769978475428260L;
	private String todoMessage;
	private String todoStatement;

	public Todo(String toDoMessage, String todoStatement) {
		this.todoMessage = toDoMessage;
		this.todoStatement = todoStatement;
	}

	public int execute(int myInstructionCounter, IntegerStack myStack) throws SuspendExecutionException {
		throw new SuspendExecutionException(myInstructionCounter, this.todoMessage, this.todoStatement);
	}

	public String toText(Code code) {
		return "Todo \"" + this.todoMessage + "-" + this.todoStatement + "\"";
	}
}
