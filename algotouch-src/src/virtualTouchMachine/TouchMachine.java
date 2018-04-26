package virtualTouchMachine;

import java.io.Serializable;
import java.util.Stack;
import virtualTouchMachine.CallMacroException;
import virtualTouchMachine.Code;
import virtualTouchMachine.CodeAddress;
import virtualTouchMachine.EndCodeException;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.TouchMachineException;

public class TouchMachine implements Serializable {
	private static final long serialVersionUID = 5511781156713018733L;
	private Code myCode;
	private IntegerStack myStack;
	private Stack<CodeAddress> myCallStack;

	public Code getCode() {
		return this.myCode;
	}

	public TouchMachine(Code code) {
		this.myCode = code;
		this.myStack = new IntegerStack();
		this.myCallStack = new Stack();
	}

	public void pushValue(int value) {
		this.myStack.push(Integer.valueOf(value));
	}

	public void run(int restartAddress) throws TouchMachineException {
		boolean resume = true;

		while (resume) {
			try {
				this.myCode.execute(restartAddress, this.myStack);
				resume = false;
			} catch (CallMacroException arg4) {
				this.myCallStack.push(new CodeAddress(this.myCode, arg4.getReturnAddress()));
				this.myCode = arg4.getCodeToExecute();
				restartAddress = 0;
			} catch (EndCodeException arg5) {
				if (!this.myCallStack.empty()) {
					CodeAddress codeAddress = (CodeAddress) this.myCallStack.pop();
					this.myCode = codeAddress.getCode();
					restartAddress = codeAddress.getAddress();
				} else {
					resume = false;
				}
			} catch (TouchMachineException arg6) {
				throw arg6;
			}
		}

	}
}
