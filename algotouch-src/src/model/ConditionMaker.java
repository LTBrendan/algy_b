package model;

import java.util.ArrayList;
import java.util.List;
import model.AlgoElement;
import model.AlgoOperator;
import model.Algorithm;
import model.ConditionMaker.ScaleState;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.TwoOperandsOperationType;

public abstract class ConditionMaker extends AlgoOperator {
	private static final long serialVersionUID = 5134707373780491861L;
	public static final int WEIGHT_SHIFT = 5;
	protected TwoOperandsOperationType testType;
	protected String testOperation;
	protected String reverseTestOperation;
	protected ScaleState state;
	protected ScaleState oldState;
	protected String currentCondition;

	public ConditionMaker(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public void update() {
		byte leftSideWeight = 0;
		byte rightSideWeight = 0;
		if (this.leftVar.getValue() > this.rightVar.getValue()) {
			this.testType = TwoOperandsOperationType.GREATER;
			this.testOperation = " > ";
			this.reverseTestOperation = " <= ";
			leftSideWeight = 5;
			rightSideWeight = -5;
		} else if (this.leftVar.getValue() < this.rightVar.getValue()) {
			this.testType = TwoOperandsOperationType.LOWER;
			this.testOperation = " < ";
			this.reverseTestOperation = " >= ";
			leftSideWeight = -5;
			rightSideWeight = 5;
		} else {
			this.testOperation = " == ";
			this.reverseTestOperation = " != ";
			this.testType = TwoOperandsOperationType.EQUAL;
		}

		this.initOperands(leftSideWeight, rightSideWeight);
	}

	public AlgoElement selectTarget(int targetX, int targetY) {
		this.target = null;
		if (this.state == ScaleState.FRESH || this.state == ScaleState.READY_TO_COMPARE) {
			if (this.leftVar.includes(targetX, targetY)) {
				this.target = this.leftVar;
			} else if (this.rightVar.includes(targetX, targetY)) {
				this.target = this.rightVar;
			}
		}

		return this.target;
	}

	public void produceExpressionCode(List<Instruction> myCode) {
	}

	public void produceAssignmentCode(List<Instruction> myCode) {
	}

	public void storeCode(AlgoElement source) {
		if (this.target == this.leftVar) {
			this.leftVar.setVisibleValue(true);
			this.leftLineOfCode = source.getSourceId();
			this.leftPanCode = new ArrayList();
			source.produceExpressionCode(this.leftPanCode);
		} else {
			this.rightVar.setVisibleValue(true);
			this.rightLineOfCode = source.getSourceId();
			this.rightPanCode = new ArrayList();
			source.produceExpressionCode(this.rightPanCode);
		}

		if (this.state == ScaleState.FRESH && this.leftVar.isVisibleValue() && this.rightVar.isVisibleValue()) {
			this.state = ScaleState.READY_TO_COMPARE;
		}

	}

	public boolean isReadyToCompare() {
		return this.state == ScaleState.READY_TO_COMPARE;
	}

	public boolean isOperating() {
		return this.state == ScaleState.OPERATING;
	}

	public void setOperating() {
		this.state = ScaleState.OPERATING;
	}

	public boolean isDone() {
		return this.state == ScaleState.DONE;
	}

	public void setDone() {
		this.state = ScaleState.DONE;
	}

	public String getCurrentCondition() {
		return this.currentCondition;
	}

	public ScaleState getState() {
		return this.state;
	}

	public TwoOperandsOperationType getCondition() {
		return this.testType;
	}

	public void setTestType(TwoOperandsOperationType testType) {
		this.testType = testType;
		switch ($SWITCH_TABLE$virtualTouchMachine$TwoOperandsOperationType()[testType.ordinal()]) {
			case 8 :
				this.testOperation = "==";
				this.reverseTestOperation = "!=";
				break;
			case 9 :
				this.testOperation = "!=";
				this.reverseTestOperation = "==";
				break;
			case 10 :
				this.testOperation = "<";
				this.reverseTestOperation = ">=";
				break;
			case 11 :
				this.testOperation = ">";
				this.reverseTestOperation = "<=";
				break;
			case 12 :
				this.testOperation = "<=";
				this.reverseTestOperation = ">";
				break;
			case 13 :
				this.testOperation = ">=";
				this.reverseTestOperation = "<";
		}

	}

	public void setConditionAsName() {
		this.setName(Algorithm.convertName(this.leftLineOfCode + this.testOperation + this.rightLineOfCode));
	}
}
