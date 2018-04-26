package virtualTouchMachine;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import virtualTouchMachine.Branch;
import virtualTouchMachine.Continue;
import virtualTouchMachine.EndBloc;
import virtualTouchMachine.EndCodeException;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.IntegerStack;
import virtualTouchMachine.SuspendExecutionException;
import virtualTouchMachine.SuspendForReadException;
import virtualTouchMachine.TouchMachineException;
import virtualTouchMachine.UnknownAddressException;
import virtualTouchMachine.Branch.BRANCH_TYPE;
import virtualTouchMachine.Code.BLOCK_TYPE;

public class Code extends LinkedList<Instruction> {
	private static final long serialVersionUID = -516094553906029109L;
	public static final String END_FROM = "End From";
	public static final String END_EVAL = "End Eval";
	public static final String END_CORE_LOOP = "End core loop";
	public static final String END_ALGORITHM = "End algorithm";
	private int currentInsertionAddress = 0;
	private Continue startFrom;
	private EndBloc endFrom;
	private Continue startEval;
	private EndBloc endEval;
	private Continue startCoreLoop;
	private EndBloc endCoreLoop;
	private Branch redo;
	private Continue startTerminate;
	private EndBloc endTerminate;
	private String name;

	public Code(String name) {
		this.name = name;
		this.startFrom = new Continue();
		this.endFrom = new EndBloc("End From");
		this.startEval = new Continue();
		this.endEval = new EndBloc("End Eval");
		this.startCoreLoop = new Continue();
		this.endCoreLoop = new EndBloc("End core loop");
		this.startTerminate = new Continue();
		this.endTerminate = new EndBloc("End algorithm");
		this.createCodeFramework();
	}

	private void createCodeFramework() {
		this.add((Instruction) this.startFrom);
		this.add((Instruction) this.endFrom);
		this.add((Instruction) this.startEval);
		this.add((Instruction) this.endEval);
		this.add((Instruction) this.startCoreLoop);
		this.add((Instruction) this.endCoreLoop);
		this.add((Instruction) this.startTerminate);
		this.add((Instruction) this.endTerminate);
	}

	public int getCurrentInsertionAdress() {
		return this.currentInsertionAddress;
	}

	public void setCurrentInsertionAdress(int currentInsertionAdress) {
		this.currentInsertionAddress = currentInsertionAdress;
	}

	public boolean add(Instruction e) {
		this.add(this.currentInsertionAddress, e);
		++this.currentInsertionAddress;
		return true;
	}

	public void add(int index, Instruction element) {
		super.add(this.currentInsertionAddress, element);
	}

	public boolean addAll(Collection<? extends Instruction> c) {
		return this.addAll(this.currentInsertionAddress, c);
	}

	public boolean addAll(int index, Collection<? extends Instruction> c) {
		boolean result = super.addAll(this.currentInsertionAddress, c);
		this.currentInsertionAddress += c.size();
		return result;
	}

	public void execute(int myInstructionCounter, IntegerStack myStack) throws TouchMachineException {
		int currentCounter = myInstructionCounter;

		while (currentCounter < this.size()) {
			Instruction i = (Instruction) this.get(currentCounter);

			try {
				currentCounter = i.execute(currentCounter, myStack);
			} catch (UnknownAddressException arg7) {
				Instruction instructionToBranch = arg7.getBranchInstruction();
				int addressToBranch = this.indexOf(instructionToBranch);
				currentCounter = addressToBranch;
			} catch (SuspendExecutionException arg8) {
				throw arg8;
			} catch (SuspendForReadException arg9) {
				throw arg9;
			} catch (TouchMachineException arg10) {
				throw arg10;
			}
		}

		throw new EndCodeException("End code");
	}

	public void clearFrom() {
		this.clearBloc(this.startFrom, this.endFrom);
	}

	public void clearEval() {
		this.clearBloc(this.startEval, this.endEval);
		if (this.redo != null) {
			this.remove(this.redo);
		}

		this.redo = null;
	}

	public void clearLoop() {
		this.clearBloc(this.startCoreLoop, this.endCoreLoop);
	}

	public void clearTerminate() {
		this.clearBloc(this.startTerminate, this.endTerminate);
	}

	public void clearBloc(Continue startInstructionBloc, EndBloc endInstructionBloc) {
		int addressToRemove = this.indexOf(startInstructionBloc) + 1;

		for (Instruction currentInstruction = (Instruction) this.get(addressToRemove); !currentInstruction
				.equals(endInstructionBloc); currentInstruction = (Instruction) this.get(addressToRemove)) {
			this.remove(addressToRemove);
		}

	}

	public void setFromInsertionAddress() {
		this.currentInsertionAddress = this.indexOf(this.endFrom);
	}

	public void setUntilInsertionAddress() {
		this.currentInsertionAddress = this.indexOf(this.startEval) + 1;
	}

	public void setCoreLoopInsertionAddress() {
		this.currentInsertionAddress = this.indexOf(this.endCoreLoop);
	}

	public void setTerminateInsertionAddress() {
		this.currentInsertionAddress = this.indexOf(this.endTerminate);
	}

	public int getFromInsertionAddress() {
		return this.indexOf(this.startFrom);
	}

	public int getCoreLoopInsertionAddress() {
		return this.indexOf(this.startCoreLoop);
	}

	public int getTerminateInsertionAddress() {
		return this.indexOf(this.startTerminate);
	}

	public void addBranchTrueTerminate() {
		this.add((Instruction) (new Branch(BRANCH_TYPE.TOP_STACK_TRUE, this.startTerminate)));
	}

	public String toString() {
		String result = "Macro " + this.name + "\n";
		int k = 0;

		for (Iterator arg3 = this.iterator(); arg3.hasNext(); ++k) {
			Instruction i = (Instruction) arg3.next();
			result = result + k + " : " + i.toText(this) + "\n";
		}

		return result;
	}

	public void setLoop() {
		if (this.redo == null) {
			this.redo = new Branch(BRANCH_TYPE.ALWAYS, this.startEval);
			super.add(this.getTerminateInsertionAddress(), this.redo);
		}

	}

	public boolean isEmpty(BLOCK_TYPE type) {
		Continue startBlock = null;
		EndBloc endBlock = null;
		switch ($SWITCH_TABLE$virtualTouchMachine$Code$BLOCK_TYPE()[type.ordinal()]) {
			case 1 :
				startBlock = this.startFrom;
				endBlock = this.endFrom;
				break;
			case 2 :
				startBlock = this.startEval;
				endBlock = this.endEval;
				break;
			case 3 :
				startBlock = this.startCoreLoop;
				endBlock = this.endCoreLoop;
				break;
			case 4 :
				startBlock = this.startTerminate;
				endBlock = this.endTerminate;
		}

		return this.indexOf(endBlock) - this.indexOf(startBlock) == 1;
	}

	public void setUntilEndAddress() {
		this.currentInsertionAddress = this.indexOf(this.endEval);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String newName) {
		this.name = newName;
	}
}
