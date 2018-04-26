package controllers.jslink;

import model.Utils;
import model.Utils.Level;
import view.main.App;

/**
 * Switch between Documentation and ALGY withc javascript request
 * @author dorian
 */
public class Switch {

	/**
	 * Application UI
	 */
	private App app;
	
	/**
	 * Create a new switch
	 * @param app Application UI
	 */
	public Switch(App app) {
		this.app = app;
	}
	
	/**
	 * Display documentation
	 */
	public void openDoc() {
		Utils.printLog(Level.LOG, "Opening documentation");
		this.app.displayDocumentation();
	}
	
	/**
	 * Display ALGY
	 */
	public void openBlockly() {
		Utils.printLog(Level.LOG, "Opening blockly workspace");
		this.app.displayBlockly();
	}
	
}
