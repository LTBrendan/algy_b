package model;

import model.AlgoElement;
import model.AlgoVariable;

public class AlgoConst extends AlgoVariable {
	private static final long serialVersionUID = 8385822528991823565L;
	private static int nbConst = 0;
	private static final int X_TOP = 0;
	private static final int Y_TOP = 96;

	public AlgoConst(String string, int constValue) {
		super(0 + nbConst % 2 * 64, 96 + nbConst / 2 * 64, 64, 64, string);
		++nbConst;
		this.value = constValue;
	}

	public AlgoElement selectTarget(int targetX, int targetY) {
		return null;
	}

	public void setValue(int value) {
	}

	public void incValue() {
	}

	public void decValue() {
	}

	public static void resetNbConst() {
		nbConst = 0;
	}
}
