package model.vars.listeners;

import virtualTouchMachine.Array;
import virtualTouchMachine.Variable;

/**
 * Define a listener for variables update and change
 * @author dorian
 * This listener is defined in WatchListener
 */
public interface VarListener {

	/**
	 * Used when a Variable is created
	 * @param var The new Variable
	 */
	void onCreate(Variable var);
	
	/**
	 * Used when an Array is created
	 * @param array The new Array
	 */
	void onCreate(Array array);
	
	/**
	 * Used when the name of a Variable change
	 * @param arr The Variable renamed
	 * @param old Old name of the Variable
	 * @param cur Current name of the Variable
	 */
	void onRename(Variable arr, String old, String cur);
	
	/**
	 * Used when the name of an Array change
	 * @param arr The Array renamed
	 * @param old Old name of the Array
	 * @param cur Current name of the Array
	 */
	void onRename(Array arr, String old, String cur);
	
	/**
	 * Used when a Variable is removed
	 * @param var The removed Variable
	 */
	void onDelete(Variable var);
	
	/**
	 * Used when an Array is removed
	 * @param array The removed Array
	 */
	void onDelete(Array array);
	
	/**
	 * Used when a Variable value is update
	 * @param var The updated Variable
	 * @param old Old value of the Variable
	 * @param cur Current value of the Variable
	 */
	void onUpdate(Variable var, String old, String cur);
	
	/**
	 * Used when an Array value is update at an index
	 * @param var Updated variable
	 * @param index Index of the update
	 * @param old Old value of the Array at this index
	 * @param cur Current value of the Array at this index
	 */
	void onUpdate(Array var, int index, String old, String cur);

}
