package model;

import model.ConditionMaker;
import model.ConditionMaker.ScaleState;
import view.AlgoTouchFrame;
import virtualTouchMachine.Code;
import virtualTouchMachine.TwoOperandsOperation;

public class ExitLoopManager extends ConditionMaker {
	private static final long serialVersionUID = 9097402183376518114L;
	private boolean firstPlace;

	public ExitLoopManager(int x, int y) {
		super(x, y, 128 + AlgoTouchFrame.scaleIcon.getIconWidth() + 20, 192);
		this.update();
		this.state = ScaleState.FRESH;
	}

	public void produceExitCaseCode(Code myCode) {
		if (this.firstPlace) {
			myCode.setUntilInsertionAddress();
		} else {
			myCode.setUntilEndAddress();
		}

		myCode.addAll(this.leftPanCode);
		myCode.addAll(this.rightPanCode);
		myCode.add(new TwoOperandsOperation(this.testType));
		myCode.addBranchTrueTerminate();
	}

	public String produceExitLineOfCode() {
		this.currentCondition = "(";
		this.currentCondition = this.currentCondition + this.leftLineOfCode;
		this.currentCondition = this.currentCondition + this.testOperation;
		this.currentCondition = this.currentCondition + this.rightLineOfCode;
		this.currentCondition = this.currentCondition + " )";
		return this.currentCondition;
	}

	public void setInsertPlace(boolean firstPlace) {
		this.firstPlace = firstPlace;
	}
}
