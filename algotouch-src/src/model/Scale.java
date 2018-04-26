package model;

import control.Messages;
import model.Algorithm;
import model.ConditionMaker;
import model.ConditionMaker.ScaleState;
import view.AlgoTouchFrame;
import virtualTouchMachine.Branch;
import virtualTouchMachine.Code;
import virtualTouchMachine.Continue;
import virtualTouchMachine.TwoOperandsOperation;
import virtualTouchMachine.Branch.BRANCH_TYPE;

public class Scale extends ConditionMaker {
	private static final long serialVersionUID = -6814193446282372437L;
	private int addressToFix;

	public Scale(int x, int y) {
		super(x, y, 128 + AlgoTouchFrame.scaleIcon.getIconWidth() + 20, 192);
		this.name = Messages.getString("SCALE");
		this.update();
		this.state = ScaleState.FRESH;
	}

	public int getAddressToFix() {
		return this.addressToFix;
	}

	public void produceBranchCode(Code myCode) {
		myCode.addAll(this.leftPanCode);
		myCode.addAll(this.rightPanCode);
		myCode.add(new TwoOperandsOperation(this.testType));
		myCode.add(new Branch(BRANCH_TYPE.TOP_STACK_FALSE, new Continue()));
		this.addressToFix = myCode.getCurrentInsertionAdress() - 1;
	}

	public String produceTestLineOfCode() {
		this.currentCondition = "(";
		this.currentCondition = this.currentCondition + this.leftLineOfCode;
		this.currentCondition = this.currentCondition + this.testOperation;
		this.currentCondition = this.currentCondition + this.rightLineOfCode;
		this.currentCondition = this.currentCondition + " )";
		String result = "if " + this.currentCondition + "{";
		this.setName(Algorithm.convertName(this.currentCondition));
		return result;
	}

	public void setAsleep(boolean asleep) {
		if (asleep) {
			this.oldState = this.state;
			this.state = ScaleState.ASLEEP;
		} else {
			this.state = this.oldState;
		}

	}

	public boolean isAsleep() {
		return this.state == ScaleState.ASLEEP;
	}

	public String getReverseCondition() {
		return this.leftLineOfCode + this.reverseTestOperation + this.rightLineOfCode;
	}
}
