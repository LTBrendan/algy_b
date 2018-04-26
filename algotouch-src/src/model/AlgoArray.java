package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import model.AlgoConst;
import model.AlgoElement;
import model.AlgoVarIndex;
import model.AlgoVariable;
import model.Algorithm.AlgoTypes;
import virtualTouchMachine.Array;
import virtualTouchMachine.IndexedAssignment;
import virtualTouchMachine.Instruction;
import virtualTouchMachine.PushIndexedVar;
import virtualTouchMachine.PushValue;

public class AlgoArray extends AlgoElement implements Array {
	private static final long serialVersionUID = 2026144882696315936L;
	public static final int ARRAY_OFFSET = 15;
	public static final int MARGIN = 30;
	protected AlgoElement myVar;
	protected List<AlgoVariable> myArray;
	protected List<AlgoVarIndex> myIndexes;
	protected int nbIndex = 0;
	private AlgoConst arrayLength;

	public AlgoArray(String name, String comment, AlgoTypes algoType, int[] content) {
		super(256, 128, 79 * content.length + 60, 124);
		this.name = name;
		this.comment = comment;
		this.elementType = algoType;
		this.myArray = new ArrayList();
		this.myIndexes = new ArrayList();

		for (int i = 0; i < content.length; ++i) {
			AlgoVariable v = new AlgoVariable(0, 0, 64, 64, name + "[" + i + "]");
			v.setElementType(algoType);
			v.setValue(content[i]);
			v.setIndex(i);
			v.setInArray(true);
			this.myArray.add(v);
		}

		this.placeArrayVariables();
	}

	public void placeArrayVariables() {
		int startX = this.x + 30;
		int startY = this.y + 30;
		int i = 0;

		for (Iterator arg4 = this.myArray.iterator(); arg4.hasNext(); ++i) {
			AlgoVariable a = (AlgoVariable) arg4.next();
			a.place(startX + 79 * i, startY);
		}

	}

	public static int[] randomContent(int mySize, int myMin, int myMax) {
		int[] result = new int[mySize];

		for (int i = 0; i < mySize; ++i) {
			result[i] = (int) Math.round(Math.random() * (double) (myMax - myMin)) + myMin;
		}

		return result;
	}

	public AlgoElement findActiveObject(int x, int y) {
		Object activeObject = null;
		if (this.includes(x, y)) {
			activeObject = this;
			Iterator arg4 = this.myArray.iterator();

			while (arg4.hasNext()) {
				AlgoElement v = (AlgoElement) arg4.next();
				if (v.includes(x, y)) {
					activeObject = v;
					break;
				}
			}
		}

		return (AlgoElement) activeObject;
	}

	public AlgoElement selectSource(int x, int y) {
		AlgoArray result = null;
		this.source = null;
		if (this.includes(x, y)) {
			Iterator arg4 = this.myArray.iterator();

			while (arg4.hasNext()) {
				AlgoElement v = (AlgoElement) arg4.next();
				if (v.includes(x, y)) {
					result = this;
					this.source = v;
					break;
				}
			}
		}

		return result;
	}

	public void move(int dx, int dy) {
		int px = Math.min(this.x, this.arrayLength.x);
		int py = Math.min(this.y, this.arrayLength.y);

		AlgoVarIndex i;
		Iterator arg5;
		for (arg5 = this.myIndexes.iterator(); arg5.hasNext(); py = Math.min(py, i.y)) {
			i = (AlgoVarIndex) arg5.next();
			px = Math.min(px, i.x);
		}

		if (px + dx < 0) {
			dx = -px;
		}

		if (py + dy < 0) {
			dy = -py;
		}

		super.move(dx, dy);
		this.arrayLength.move(dx, dy);
		this.placeArrayVariables();
		arg5 = this.myIndexes.iterator();

		while (arg5.hasNext()) {
			i = (AlgoVarIndex) arg5.next();
			i.move(dx, dy);
		}

	}

	public AlgoVariable getClosestVariable(int x, int y) {
		Object result = null;
		Iterator arg4 = this.myArray.iterator();

		while (arg4.hasNext()) {
			AlgoVariable v = (AlgoVariable) arg4.next();
			if (v.includes(x, y)) {
				return v;
			}
		}

		return (AlgoVariable) result;
	}

	public AlgoVariable get(int value) {
		AlgoVariable result = null;
		if (value >= 0 && value < this.myArray.size()) {
			result = (AlgoVariable) this.myArray.get(value);
		}

		return result;
	}

	public AlgoElement getSourceCopy() {
		return this.source.getSourceCopy();
	}

	public List<AlgoVariable> getVariableList() {
		return this.myArray;
	}

	public AlgoVariable findActiveVar(int x, int y) {
		AlgoVariable activeObject = null;
		if (this.includes(x, y)) {
			Iterator arg4 = this.myArray.iterator();

			while (arg4.hasNext()) {
				AlgoVariable v = (AlgoVariable) arg4.next();
				if (v.includes(x, y)) {
					activeObject = v;
					break;
				}
			}
		}

		return activeObject;
	}

	public AlgoVariable findActiveValue(int x, int y) {
		return this.findActiveVar(x, y);
	}

	public AlgoElement selectTarget(int targetX, int targetY) {
		AlgoArray result = null;
		this.target = null;
		if (this.includes(targetX, targetY)) {
			Iterator arg4 = this.myArray.iterator();

			while (arg4.hasNext()) {
				AlgoElement v = (AlgoElement) arg4.next();
				if (v.includes(targetX, targetY)) {
					result = this;
					this.target = v;
					break;
				}
			}
		}

		return result;
	}

	public void setHighLight(boolean highLight) {
		this.target.setHighLight(highLight);
	}

	public int getSourceValue() {
		return this.source.getSourceValue();
	}

	public void setTargetValue(int sourceValue) {
		this.target.setTargetValue(sourceValue);
	}

	public String getTargetId() {
		return this.target.getId();
	}

	public String getSourceId() {
		return this.source.getId();
	}

	public void incrementNbIndex() {
		++this.nbIndex;
	}

	public int getNbIndex() {
		return this.nbIndex;
	}

	public void produceExpressionCode(List<Instruction> myCode) {
		myCode.add(new PushValue(((AlgoVariable) this.source).getIndex()));
		myCode.add(new PushIndexedVar(this));
	}

	public void produceAssignmentCode(List<Instruction> myCode) {
		myCode.add(new PushValue(((AlgoVariable) this.target).getIndex()));
		myCode.add(new IndexedAssignment(this));
	}

	public int getValue(int index) {
		return this.get(index).getValue();
	}

	public void setName(String name) {
		super.setName(name);

		for (int i = 0; i < this.myArray.size(); ++i) {
			this.get(i).setName(name + "[" + i + "]");
		}

	}

	public void setValue(int index, int value) {
		this.get(index).setValue(value);
	}

	public int getSize() {
		return this.myArray.size();
	}

	public void setContent(int[] content) {
		for (int i = 0; i < content.length; ++i) {
			this.get(i).setValue(content[i]);
		}

	}

	public int[] getContent() {
		int[] content = new int[this.getSize()];

		for (int i = 0; i < this.getSize(); ++i) {
			content[i] = this.getValue(i);
		}

		return content;
	}

	public static void shuffleContent(int[] content) {
		int n = content.length;

		for (int i = 0; i < n; ++i) {
			int tmp = content[i];
			int j = (int) (Math.random() * (double) n);
			content[i] = content[j];
			content[j] = tmp;
		}

	}

	public void addIndex(AlgoVarIndex index) {
		this.myIndexes.add(index);
	}

	public void addLengthConst(AlgoConst arrayLength) {
		this.arrayLength = arrayLength;
	}

	public AlgoConst getLengthConst() {
		return this.arrayLength;
	}

	public AlgoElement getConstantLength() {
		return this.arrayLength;
	}

	public void removeIndex(AlgoVarIndex v) {
		this.myIndexes.remove(v);
	}
}
