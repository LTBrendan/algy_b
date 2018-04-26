package model;

import java.util.List;
import model.AlgoElement;
import model.AlgoVariable;
import view.AlgoTouchFrame;
import virtualTouchMachine.Instruction;

public abstract class AlgoOperator extends AlgoElement {
	private static final long serialVersionUID = -2186511965757311305L;
	protected AlgoVariable leftVar = new AlgoVariable("", "");
	protected AlgoVariable rightVar;
	protected List<Instruction> leftPanCode;
	protected List<Instruction> rightPanCode;
	protected String leftLineOfCode;
	protected String rightLineOfCode;

	public abstract AlgoElement selectTarget(int arg0, int arg1);

	public AlgoOperator(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.leftVar.setVisibleValue(false);
		this.rightVar = new AlgoVariable("", "");
		this.rightVar.setVisibleValue(false);
	}

	protected void initOperands(int leftSideWeight, int rightSideWeight) {
		this.leftVar.place(this.x + 5, this.y + 32 + leftSideWeight);
		this.rightVar.place(this.x + 5 + 64 + AlgoTouchFrame.addIcon.getIconWidth() + 10,
				this.y + 32 + rightSideWeight);
	}

	public AlgoElement getSourceCopy() {
		return null;
	}

	public AlgoVariable findActiveVar(int x, int y) {
		AlgoVariable result = null;
		if (this.leftVar.includes(x, y)) {
			result = this.leftVar;
		} else if (this.rightVar.includes(x, y)) {
			result = this.rightVar;
		}

		return result;
	}

	public AlgoElement getLeft() {
		return this.leftVar;
	}

	public AlgoElement getRight() {
		return this.rightVar;
	}

	public void move(int dx, int dy) {
		int oldX = this.x;
		int oldY = this.y;
		super.move(dx, dy);
		dx = this.x - oldX;
		dy = this.y - oldY;
		this.leftVar.move(dx, dy);
		this.rightVar.move(dx, dy);
	}

	public AlgoElement selectSource(int x, int y) {
		return null;
	}

	public void setHighLight(boolean highLight) {
		this.target.setHighLight(highLight);
	}

	public int getSourceValue() {
		return 0;
	}

	public void setTargetValue(int sourceValue) {
		this.target.setTargetValue(sourceValue);
	}

	public String getTargetId() {
		return "// " + this.target.getId();
	}

	public String getSourceId() {
		return "// ";
	}
}
