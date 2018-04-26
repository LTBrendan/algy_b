package model.vars;

import java.util.Objects;

import model.vars.listeners.VarListener;
import virtualTouchMachine.Variable;

/**
 * Define an Integer Variable type
 * @author dorian
 */
public class IntVar implements Variable, Watchable<IntVar> {

	/**
	 * Listener to watch any update of the variable
	 */
	protected VarListener listener;
	
	/**
	 * Name of the variable
	 */
	private String name;

	/**
	 * Value of the variable
	 */
	private int value;

	/**
	 * Create a new variable
	 * @param name Name of this variable
	 */
	public IntVar(String name) {
		super();
		this.name = name;
	}
	
	@Override
	public void setListener(VarListener listener) {
		this.listener = Objects.requireNonNull(listener);
		this.listener.onCreate(this);
	}
	
	@Override
	public void delete() {
		this.listener.onDelete(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getValue() {
		return value;
	}
	

	@Override
	public void setValue(int val) {
		int last = this.value;
		this.value = val;
		if(this.listener != null)
			if(this instanceof CharVar)
				listener.onUpdate(this, String.valueOf((char)last), String.valueOf((char)val));
			else
				listener.onUpdate(this, String.valueOf(last), String.valueOf(val));
		
	}

	@Override
	public IntVar rename(String newname) {
		String old = this.name;
		this.name = newname;
		if(listener != null)
			listener.onRename(this, old, newname);
		return this;
	}
}
