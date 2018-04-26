package model.vars;

/**
 * Define a constant Integer Variable type
 * @author dorian
 */
public class ConstantVar extends IntVar {

	/**
	 * Create a new constant integer
	 * @param name Name of the variable
	 * @param value Value of the variable
	 */
	public ConstantVar(String name, int value) {
		super(name);
		super.setValue(value);
	}

	/**
	 * Set a new value to the constant (impossible so empty)
	 */
	@Override
	public void setValue(int val) {
	}

}
