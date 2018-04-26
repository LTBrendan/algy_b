package model;

import java.util.ArrayList;
import java.util.List;
import model.AlgoElement;
import model.AlgoOperator;
import model.AlgoVariable;
import view.AlgoTouchFrame;
import virtualTouchMachine.Code;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.TwoOperandsOperation;
import virtualTouchMachine.TwoOperandsOperationType;

public class AlgoAddSub extends AlgoOperator {
	private static final long serialVersionUID = 6689311778621987893L;
	private AlgoVariable result;
	private AlgoElement resultTarget;
	private boolean ready = false;
	private TwoOperandsOperationType typeOfOperation;
	private String operationSymbol;

	public AlgoAddSub(int x, int y, String operation, AlgoElement varSelected) {
		super(x, y, 128 + AlgoTouchFrame.addIcon.getIconWidth() + 20, 192);
		this.initOperands(0, 0);
		this.resultTarget = varSelected;
		this.result = new AlgoVariable("(" + varSelected.getName() + ")", "tmp result");
		this.result.setVisibleValue(false);
		this.result.place(x + 64 - 2, y + 96 + 5);
		if (operation.equals("ADD")) {
			this.typeOfOperation = TwoOperandsOperationType.ADD;
			this.operationSymbol = "+";
		} else if (operation.equals("SUBTRACT")) {
			this.typeOfOperation = TwoOperandsOperationType.SUB;
			this.operationSymbol = "-";
		} else if (operation.equals("DIVIDE")) {
			this.typeOfOperation = TwoOperandsOperationType.DIV;
			this.operationSymbol = "/";
		} else if (operation.equals("MULTIPLY")) {
			this.typeOfOperation = TwoOperandsOperationType.MULT;
			this.operationSymbol = "*";
		} else if (operation.equals("REMAINDER")) {
			this.typeOfOperation = TwoOperandsOperationType.MOD;
			this.operationSymbol = "%";
		}

	}

	public AlgoElement selectTarget(int targetX, int targetY) {
		this.target = null;
		if (this.leftVar.includes(targetX, targetY)) {
			this.target = this.leftVar;
		} else if (this.rightVar.includes(targetX, targetY)) {
			this.target = this.rightVar;
		}

		return this.target;
	}

	public void produceExpressionCode(List<Instruction> myCode) {
	}

	public void produceAssignmentCode(List<Instruction> myCode) {
	}

	public AlgoElement getResult() {
		return this.result;
	}

	public void move(int dx, int dy) {
		int oldX = this.x;
		int oldY = this.y;
		super.move(dx, dy);
		dx = this.x - oldX;
		dy = this.y - oldY;
		this.result.move(dx, dy);
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

		if (this.leftVar.isVisibleValue() && this.rightVar.isVisibleValue()) {
			this.ready = true;
			switch ($SWITCH_TABLE$virtualTouchMachine$TwoOperandsOperationType()[this.typeOfOperation.ordinal()]) {
				case 1 :
					this.result.setValue(this.leftVar.getValue() + this.rightVar.getValue());
					break;
				case 2 :
					this.result.setValue(this.leftVar.getValue() - this.rightVar.getValue());
					break;
				case 3 :
					this.result.setValue(this.leftVar.getValue() * this.rightVar.getValue());
					break;
				case 4 :
					this.result.setValue(this.leftVar.getValue() / this.rightVar.getValue());
					break;
				case 5 :
					this.result.setValue(this.leftVar.getValue() % this.rightVar.getValue());
			}

			this.result.setVisibleValue(true);
		}

	}

	public boolean isReady() {
		return this.ready;
	}

	public String terminate() {
		this.resultTarget.setTargetValue(this.result.getValue());
		String lineOfCode = this.resultTarget.getTargetId() + " = " + this.leftLineOfCode + " " + this.operationSymbol
				+ " " + this.rightLineOfCode + " ;";
		return lineOfCode;
	}

	public void produceOperatorCode(Code myCode) {
		myCode.addAll(this.leftPanCode);
		myCode.addAll(this.rightPanCode);
		myCode.add(new TwoOperandsOperation(this.typeOfOperation));
		this.resultTarget.produceAssignmentCode(myCode);
	}

	public TwoOperandsOperationType getTypeOfOperation() {
		return this.typeOfOperation;
	}
}
