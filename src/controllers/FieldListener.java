package controllers;

import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import view.component.PopupTable;

/**
 * Field listener for the popup table listen to valueChoice
 * @author dorian
 */
public class FieldListener implements EventHandler<KeyEvent> {
	
	/**
	 * The type of the current PopupTable (INTEGER or CHARACTER)
	 */
	private String choice;
	
	/**
	 * All fields to watch witch a boolean for ok or no (matching input)
	 */
	private HashMap<TextField, Boolean> map;
	
	/**
	 * Create the listener
	 * @param choice the type of values
	 * @param map Fields listened with OK or not
	 */
	public FieldListener(String choice, HashMap<TextField, Boolean> map) {
		this.choice = choice;
		this.map = map;
	}

	/**
	 * Launched when a key is typed in valueChoice
	 */
	@Override
	public void handle(KeyEvent event) {
		TextField text = (TextField)event.getSource();
		KeyCode code = event.getCode();
		
		handling(text, code);
	}
	
	/**
	 * Core method of the listener which define if the field input is matching or not
	 * @param text Field modified
	 * @param code Key pressed
	 */
	public void handling(TextField text, KeyCode code) {
		// split if the field is the valueChoice with the defined separator
		String[] res = text.getText().split(" : ");
		
		// set field ok display by default
		text.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
		map.replace(text, true);
		String newString = "";
		
		// if BACK SPACE is pressed delete separator if its the last component of the string
		if(code == KeyCode.BACK_SPACE) {
			String str = text.getText();
			try {
				if(str.endsWith(" : ")) str = str.substring(0, str.length()-3);
				else if(str.endsWith(" :")) str = str.substring(0, str.length()-2);
			}
			catch(Exception e) {str = "";}
			text.setText(str);
			text.positionCaret(str.length());
		}
		// Check for INTEGER type
		else if(choice.equals("INTEGER")) {
			if(code == KeyCode.SPACE) {
				String str = text.getText();
				str = str.substring(0, str.length()-1);
				str = str + " : ";
				text.setText(str);
				text.positionCaret(str.length());
			}
			for(String s : res) {
				if(s.length() > 0) {
					try {Integer.parseInt(s);}
					catch(NumberFormatException e) {
						text.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
						map.replace(text, false);
					}
				}
				else if(s.length() == 0) {
					text.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
					map.replace(text, false);
				}
			}
		}
		// Check for CHARACTER type
		else if(choice.equals("CHAR")) {
			for(String s : res) {
				if(s.length() > 1) {
					newString += s.charAt(0) + " : ";
				}
				else if(s.length() == 0) {
					text.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
					map.replace(text, false);
				}
				else {
					newString += s + " : ";
				}
			}
			text.setText(newString);
			text.positionCaret(newString.length());			
		}
		
		// At least check if everything needed is check to enable OK button
		PopupTable.getInstance().check();
	}

}
