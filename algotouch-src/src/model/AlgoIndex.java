package model;

import java.util.List;
import model.AlgoArray;
import model.AlgoElement;
import model.AlgoVariable;
import virtualTouchMachine.IndexedAssignment;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.PushIndexedVar;
import virtualTouchMachine.PushVariable;

public class AlgoIndex extends AlgoElement {
	private static final long serialVersionUID = -6823829766564394045L;
	protected AlgoVariable myIndex = null;
	protected AlgoArray myAlgoArray = null;
	private int endArrowX;
	private int endArrowY;

	public AlgoIndex(AlgoVariable myIndex, AlgoArray myAlgoArray) {
		super(0, 0, 64, 64);
		this.myIndex = myIndex;
		this.myAlgoArray = myAlgoArray;
		this.myAlgoArray.incrementNbIndex();
		this.updatePosition();
	}

	public void updatePosition() {
		this.setName(this.myAlgoArray.getName() + '[' + this.myIndex.getName() + ']');
		AlgoVariable indexedVar = this.getIndexedVariable();
		this.endArrowY = this.myAlgoArray.getY() + this.myAlgoArray.getHeight() - 32;
		if (indexedVar != null) {
			this.endArrowX = indexedVar.getX() + 16;
		} else if (this.myIndex.getValue() < 0) {
			this.endArrowX = this.myAlgoArray.getX() + 5;
		} else {
			this.endArrowX = this.myAlgoArray.getX() + this.myAlgoArray.getWidth() - 32 - 5;
		}

		this.x = this.myIndex.getX();
		this.y = this.myIndex.getY() - 64 - 5;
	}

	public AlgoIndex(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public AlgoElement getSourceCopy() {
		AlgoElement copy = null;
		AlgoVariable indexedVariable = this.getIndexedVariable();
		if (indexedVariable != null) {
			copy = indexedVariable.getSourceCopy();
			copy.place(this.x, this.y);
		}

		return copy;
	}

	public AlgoVariable findActiveVar(int x, int y) {
		AlgoVariable indexedVariable = this.getIndexedVariable();
		indexedVariable.setName(this.myAlgoArray.getId() + "[" + this.myIndex.getId() + "]");
		return indexedVariable;
	}

	public AlgoVariable getVariable() {
		return this.myIndex;
	}

	public AlgoElement selectSource(int x, int y) {
		if (this.includes(x, y)) {
			this.source = this;
		} else {
			this.source = null;
		}

		return this.source;
	}

	public void setHighLight(boolean highLight) {
		this.highLight = highLight;
	}

	public AlgoElement selectTarget(int targetX, int targetY) {
		if (this.includes(targetX, targetY)) {
			this.target = this;
		} else {
			this.target = null;
		}

		return this.target;
	}

	public int getSourceValue() {
		return this.getIndexedVariable().getSourceValue();
	}

	private AlgoVariable getIndexedVariable() {
		AlgoVariable indexedVariable = this.myAlgoArray.get(this.myIndex.getValue());
		return indexedVariable;
	}

	public void setTargetValue(int sourceValue) {
		this.getIndexedVariable().setTargetValue(sourceValue);
	}

	public String getTargetId() {
		return this.myAlgoArray.getId() + "[" + this.myIndex.getId() + "]";
	}

	public String getSourceId() {
		return this.myAlgoArray.getId() + "[" + this.myIndex.getId() + "]";
	}

	public void produceExpressionCode(List<Instruction> myCode) {
		myCode.add(new PushVariable(this.myIndex));
		myCode.add(new PushIndexedVar(this.myAlgoArray));
	}

	public void produceAssignmentCode(List<Instruction> myCode) {
		myCode.add(new PushVariable(this.myIndex));
		myCode.add(new IndexedAssignment(this.myAlgoArray));
	}

	public AlgoArray getArray() {
		return this.myAlgoArray;
	}

	public boolean isOutOfBounds() {
		return this.getIndexedVariable() == null;
	}

	public int getendArrowX() {
		return this.endArrowX;
	}

	public int getEndArrowY() {
		return this.endArrowY;
	}
}
