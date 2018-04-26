package controllers;

import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import view.component.PopupTable;

/**
 * Listen to the field value of pouptable
 * @author dorian
 */
public class ValueListener implements EventHandler<KeyEvent> {

	/**
	 * The current type of the popup
	 */
	private String choice;
	
	/**
	 * List of all field listened with OK or not
	 */
	private HashMap<TextField, Boolean> map;
	
	/**
	 * Constructor of the listener
	 * @param choice Current type of the popup
	 * @param map All fields lsitened with mathed or not
	 */
	public ValueListener(String choice, HashMap<TextField, Boolean> map) {
		this.choice = choice;
		this.map = map;
	}

	/**
	 * Performed when a key pressed on value field
	 */
	@Override
	public void handle(KeyEvent event) {
		TextField text = (TextField)event.getSource();		
		handling(text);
	}
	
	/**
	 * Check if the input is correct or not
	 * @param text The field modified
	 */
	public void handling(TextField text) {
		// set default checked
		map.replace(text, true);
		text.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");

		// check for type INTEGER
		if(choice.equals("INTEGER")) {
			try {Integer.parseInt(text.getText());}
			catch(NumberFormatException e) {
				text.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				map.replace(text, false);
			}
		}
		// check for type CHAR
		else if(choice.equals("CHAR")) {
			if(text.getText().length() > 1) {
				text.setText("" + text.getText().charAt(0));
				text.positionCaret(text.getText().length());
				text.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
			}
			else if(text.getText().length() == 0) {
				map.replace(text, false);
				text.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			}
		}
		
		// At least check if everything needed is check to enable OK button
		PopupTable.getInstance().check();
	}

}
