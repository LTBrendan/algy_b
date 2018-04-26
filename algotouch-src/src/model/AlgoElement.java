package model;

import java.io.Serializable;
import java.util.List;
import model.AlgoVariable;
import model.Algorithm.AlgoTypes;
import virtualTouchMachine.Instruction;

public abstract class AlgoElement implements Serializable {
	private static final long serialVersionUID = -574145145490546266L;
	public static final char DEFAULT_ID_START_CHAR = '$';
	public static final char DEFAULT_ID_END_CHAR = '#';
	public static final String DEFAULT_NAME = "";
	public static final String DEFAULT_COMMENT = "";
	public static final char DEFAULT_SYSTEM_CHAR = '_';
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected boolean highLight = false;
	protected String name;
	protected String comment;
	protected AlgoTypes elementType;
	private boolean selected;
	protected AlgoElement source;
	protected AlgoElement target;
	protected static boolean blind = false;

	public AlgoElement(int x, int y, int width, int height) {
		this.elementType = AlgoTypes.INT;
		this.selected = false;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.name = "";
		this.comment = "";
	}

	public boolean includes(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
	}

	public abstract AlgoElement getSourceCopy();

	public AlgoElement findActiveObject(int x, int y) {
		AlgoElement active = null;
		if (this.includes(x, y)) {
			active = this;
		}

		return active;
	}

	public void move(int dx, int dy) {
		this.x += dx;
		if (this.x < 0) {
			this.x = 0;
		}

		this.y += dy;
		if (this.y < 0) {
			this.y = 0;
		}

	}

	public void place(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public abstract void setHighLight(boolean arg0);

	public static String getId(String string) {
		return '$' + string + '#';
	}

	public String getId() {
		return getId(this.name);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public AlgoTypes getElementType() {
		return this.elementType;
	}

	public void setElementType(AlgoTypes elementType) {
		this.elementType = elementType;
	}

	public boolean isHighLighted() {
		return this.highLight;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public abstract AlgoVariable findActiveVar(int arg0, int arg1);

	public static boolean isBlind() {
		return blind;
	}

	public static void setBlind(boolean blind) {
		AlgoElement.blind = blind;
	}

	public AlgoVariable findActiveValue(int x, int y) {
		return null;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public abstract AlgoElement selectSource(int arg0, int arg1);

	public abstract AlgoElement selectTarget(int arg0, int arg1);

	public abstract int getSourceValue();

	public abstract void setTargetValue(int arg0);

	public abstract String getTargetId();

	public abstract String getSourceId();

	public abstract void produceExpressionCode(List<Instruction> arg0);

	public abstract void produceAssignmentCode(List<Instruction> arg0);
}
