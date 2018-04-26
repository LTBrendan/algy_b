package model.vars;

/**
 * Define a character variable type
 * @author dorian
 */
public class CharVar extends IntVar{

	/**
	 * Create a new character variable
	 * @param name Name of the variable
	 */
	public CharVar(String name) {
		super(name);
	}
	
	/**
	 * Set a new value to this variable
	 */
	@Override
	public void setValue(int val) {
		super.setValue(val);
	}
	

}
