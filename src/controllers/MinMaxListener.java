package controllers;

import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import view.component.PopupTable;
/**
 * Min max listener for fields of PopupTable
 * @author dorian
 */
public class MinMaxListener implements EventHandler<KeyEvent> {
	
	/**
	 * Min field listened
	 */
	private TextField min;
	
	/**
	 * Max field listened
	 */
	private TextField max;
	
	/**
	 * Type of the current PopupTable
	 */
	private String choice;
	
	/**
	 * All fields to watch witch a boolean for ok or no (matching input)
	 */
	private HashMap<TextField, Boolean> map;
	
	/**
	 * Constructor of the class
	 * @param min Min field
	 * @param max Max Field
	 * @param choice Type of the popup
	 * @param map All fields listened
	 */
	public MinMaxListener(TextField min, TextField max, String choice, HashMap<TextField, Boolean> map) {
		this.min = min;
		this.max = max;
		this.choice = choice;
		this.map = map;
	}

	/**
	 * Launched when a key is pressed in min or max
	 */
	@Override
	public void handle(KeyEvent event) {
		TextField text = (TextField)event.getSource();
		handling(text);
		
	}
	
	/**
	 * Core method of the listener which define if the field input is matching or not
	 * @param text Field modified
	 */
	public void handling(TextField text) {
		// check for type INTEGER
		if(choice.equals("INTEGER")) {
			try {
				int val1 = Integer.parseInt(min.getText());
				int val2 = Integer.parseInt(max.getText());
				if(val1 <= val2) {
					min.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
					max.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
					map.replace(min, true);
					map.replace(max, true);
				}
				else {
					min.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
					max.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
					map.replace(min, false);
					map.replace(max, false);
				}
			} catch(NumberFormatException e) {
				text.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
			}
		}
		// check for type CHARS
		else if(choice.equals("CHAR")) {
			char char1 = 'b';
			char char2 = 'a';
			try {
				char1 = min.getText().charAt(0);
				min.setText(char1 + "");
				min.positionCaret(1);
			}
			catch(Exception e) {
				min.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				map.replace(min, false);
			}
			try {
				char2 = max.getText().charAt(0);
				max.setText("" + char2);
				max.positionCaret(1);
			}
			catch(Exception e) {
				max.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				map.replace(max, false);
			}
			
			if(char1 <= char2) {
				min.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
				max.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
				map.replace(min, true);
				map.replace(max, true);
			}
			else {
				min.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				max.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
				map.replace(min, false);
				map.replace(max, false);
			}
		}
		
		// At least check if everything needed is check to enable OK button
		PopupTable.getInstance().check();
	}
}
