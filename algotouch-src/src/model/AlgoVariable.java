package model;

import java.util.List;
import model.AlgoElement;
import virtualTouchMachine.Assignment;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.PushVariable;
import virtualTouchMachine.Variable;

public class AlgoVariable extends AlgoElement implements Variable {
	private static final long serialVersionUID = 5299107106888289843L;
	public static int nbVariables = 0;
	public static final int WIDTH = 64;
	public static final int HEIGHT = 64;
	public static final int MARGIN = 5;
	public static final int BOX_LEFT = 133;
	public static final int BOX_TOP = 96;
	protected int value = 0;
	protected boolean visibleValue = true;
	protected boolean inArray = false;
	protected int index = -1;

	public AlgoVariable(int x, int y, int width, int height, String name) {
		super(x, y, width, height);
		this.setName(name);
	}

	public AlgoVariable(String name, String comment) {
		super(133, 96, 64, 64);
		++nbVariables;
		this.setName(name);
		this.setComment(comment);
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isVisibleValue() {
		return this.visibleValue;
	}

	public void setVisibleValue(boolean visibleValue) {
		this.visibleValue = visibleValue;
	}

	public void incValue() {
		++this.value;
	}

	public void decValue() {
		--this.value;
	}

	public void setInArray(boolean isInArray) {
		this.inArray = isInArray;
	}

	public boolean isInArray() {
		return this.inArray;
	}

	public int getIndex() {
		return this.index;
	}

	public AlgoElement getSourceCopy() {
		AlgoVariable result = new AlgoVariable(this.x, this.y, this.width, this.height, "");
		result.elementType = this.elementType;
		result.value = this.value;
		return result;
	}

	public AlgoVariable findActiveVar(int x, int y) {
		AlgoVariable result = null;
		if (this.includes(x, y)) {
			result = this;
		}

		return result;
	}

	public AlgoVariable findActiveValue(int x, int y) {
		return this.findActiveVar(x, y);
	}

	public AlgoElement selectSource(int x, int y) {
		if (this.includes(x, y)) {
			this.source = this;
		} else {
			this.source = null;
		}

		return this.source;
	}

	public AlgoElement selectTarget(int targetX, int targetY) {
		if (this.includes(targetX, targetY)) {
			this.target = this;
		} else {
			this.target = null;
		}

		return this.target;
	}

	public void setHighLight(boolean highLight) {
		this.highLight = highLight;
	}

	public int getSourceValue() {
		return this.getValue();
	}

	public void setTargetValue(int sourceValue) {
		this.setValue(sourceValue);
	}

	public String getTargetId() {
		return this.getId();
	}

	public String getSourceId() {
		return this.getId();
	}

	public void produceExpressionCode(List<Instruction> myCode) {
		myCode.add(new PushVariable(this));
	}

	public void produceAssignmentCode(List<Instruction> myCode) {
		myCode.add(new Assignment(this));
	}

	public void setIndex(int i) {
		this.index = i;
	}

	public void move(int dx, int dy) {
		if (!this.inArray) {
			super.move(dx, dy);
		}

	}

	public static void resetNbVariables() {
		nbVariables = 0;
	}
}
