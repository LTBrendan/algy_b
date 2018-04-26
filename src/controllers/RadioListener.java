package controllers;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import view.component.PopupTable;

/**
 * Listener for the radio buttons of the popup table
 * @author dorian
 */
public class RadioListener implements EventHandler<ActionEvent> {
	
	/**
	 * List of all radio buttons listened
	 */
	private ArrayList<RadioButton> list;
	
	/**
	 * Constructor of the listener
	 * @param list List of radio buttons
	 */
	public RadioListener(ArrayList<RadioButton> list) {
		this.list = list;
		for(RadioButton rad: list) {
			rad.setOnAction(this);
		}
	}

	/**
	 * Performed when a radiobutton is clicked
	 */
	@Override
	public void handle(ActionEvent event) {
		RadioButton src = (RadioButton)event.getSource();
		
		// set only last one selected
		for(RadioButton rad: list) {
			rad.setSelected(false);
		}
		src.setSelected(true);
		
		// check if everything needed is check to enable OK button
		PopupTable.getInstance().check();
	}
}
