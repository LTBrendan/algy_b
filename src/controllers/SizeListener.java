package controllers;

import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import view.component.PopupTable;

/**
 * Listener for the size of the popup table
 * @author dorian
 */
public class SizeListener implements EventHandler<KeyEvent> {
	
	/**
	 * List of all listened fields with OK or not
	 */
	private HashMap<TextField, Boolean> map;
	
	/**
	 * Constructor of the lsitener
	 * @param map The list of all fields listened and OK or not (matching or not)
	 */
	public SizeListener(HashMap<TextField, Boolean> map) {
		this.map = map;
	}
	
	/**
	 * Launched at each key pressed on size field
	 */
	@Override
	public void handle(KeyEvent event) {
		TextField text = (TextField)event.getSource();
		
		/**
		 * For the field to match witch a valid integer
		 */
		try {
			int size = Integer.parseInt(text.getText());
			
			if(size > 0) {
				map.replace(text, true);
				text.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
			}
			else {
				text.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				map.replace(text, false);
			}
			
		} catch(NumberFormatException e) {
			text.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			map.replace(text, false);
		}
		
		
		// At least check if everything needed is check to enable OK button
		PopupTable.getInstance().check();
		
	}
}
