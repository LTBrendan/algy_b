package model;

import java.util.List;
import model.AlgoElement;
import model.AlgoVariable;
import virtualTouchMachine.Instruction;

public class AlgoMacroCode extends AlgoElement {
	private static final long serialVersionUID = -6332564722841069556L;
	private static int nbMacros = 0;

	public AlgoMacroCode(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public AlgoMacroCode(String name, String comment) {
		this(nbMacros * 64 * 2, 0, 128, 96);
		++nbMacros;
		this.setName(name);
		this.setComment(comment);
	}

	public AlgoElement getSourceCopy() {
		return null;
	}

	public void setHighLight(boolean highLight) {
	}

	public AlgoVariable findActiveVar(int x, int y) {
		return null;
	}

	public AlgoElement selectSource(int x, int y) {
		return null;
	}

	public AlgoElement selectTarget(int targetX, int targetY) {
		return null;
	}

	public int getSourceValue() {
		return 0;
	}

	public void setTargetValue(int sourceValue) {
	}

	public String getTargetId() {
		return null;
	}

	public String getSourceId() {
		return null;
	}

	public void produceExpressionCode(List<Instruction> myCode) {
	}

	public void produceAssignmentCode(List<Instruction> myCode) {
	}

	public static void resetNbMacros() {
		nbMacros = 0;
	}
}
