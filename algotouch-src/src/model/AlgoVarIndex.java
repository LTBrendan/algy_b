package model;

import model.AlgoArray;
import model.AlgoVariable;

public class AlgoVarIndex extends AlgoVariable {
	private static final long serialVersionUID = -751400733921817700L;
	protected AlgoArray myIndexedArray = null;

	public AlgoVarIndex(String name, String comment) {
		super(name, comment);
	}

	public AlgoArray getMyIndexedArray() {
		return this.myIndexedArray;
	}

	public void setMyIndexedArray(AlgoArray myIndexedArray) {
		this.myIndexedArray = myIndexedArray;
		if (myIndexedArray != null) {
			myIndexedArray.addIndex(this);
		}

	}
}
