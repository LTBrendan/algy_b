package model.vars;

import java.util.Objects;
import java.util.Optional;

import model.vars.listeners.VarListener;
import virtualTouchMachine.Array;

/**
 * Define an integer array type
 * @author dorian
 */
public class IntArray implements Array, Watchable<IntArray> {

	/**
	 * Listener which watch all changes to this variable
	 */
	protected VarListener listener;

	/**
	 * Name of the array
	 */
	private String name;

	/**
	 * Values of the array
	 */
	private Integer[] values;

	/**
	 * Create a new Integer Array
	 * @param name Name of the array
	 * @param values values of the array
	 */
	public IntArray(String name, Integer[] values) {
		this.name = name;
		redefine(values);
	}
	
	/**
	 * Redefine the array
	 * @param values The array updated
	 */
	public void redefine(Integer[] values) {
		this.values = Optional.ofNullable(values).orElse(new Integer[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		Optional.ofNullable(listener).ifPresent(l -> l.onCreate (this));
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getSize() {
		return values.length;
	}

	@Override
	public int getValue(int x) {
		return values[x];
	}
	
	/**
	 * Get all the values of this array
	 * @return the values of array
	 */
	public Integer[] getValues() {
		return values;
	}

	@Override
	public void setValue(int index, int val) {
		int last = this.values[index];
		this.values[index] = val;
		if(this.listener != null)
			if(this instanceof CharArray)
				listener.onUpdate(this, index, String.valueOf((char)last), String.valueOf((char)val));
			else
				listener.onUpdate(this, index, String.valueOf(last), String.valueOf(val));
	}

	@Override
	public void setListener(VarListener listener) {
		this.listener = Objects.requireNonNull(listener);
		this.listener.onCreate(this);
	}

	@Override
	public void delete() {
		if (listener != null)
			this.listener.onDelete(this);
	}

	@Override
	public IntArray rename(String newname) {
		String old = this.name;
		this.name = newname;
		if(listener != null)
			listener.onRename(this, old, newname);
		return this;
	}
	


}
