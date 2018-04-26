package model.vars;

import model.vars.listeners.VarListener;

/**
 * Define functions for a watchable variable so it could be monitored
 * @author dorian
 * @author Daphnis
 * @param <T> Type of the variable
 */
public interface Watchable<T> {
	
	/**
	 * Define the listener for a variable
	 * @param listener the new listener - should not be null
	 */
	void setListener(VarListener listener);
	
	/**
	 * Delete the variable from the program
	 */
	void delete();
	
	/**
	 * Rename the variable
	 * @param newname the new name
	 * @return the variable
	 */
	T rename(String newname);
}
