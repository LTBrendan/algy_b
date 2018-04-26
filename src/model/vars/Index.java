package model.vars;

/**
 * Index variable type
 * @author Daphnis
 *
 */
public class Index extends IntVar {

	private IntArray array;
	
	/**
	 * Create an index with the associated array
	 * @param name the name of the variable
	 * @param array the name of the associated array
	 */
	public Index(String name, IntArray array) {
		super(name);
		this.array = array;
	}

	/**
	 * Get the associated array
	 * @return the associated array
	 */
	public IntArray getAssociatedArray() {
		return array;
	}
	
	

}
