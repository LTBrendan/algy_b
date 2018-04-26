package view.component;

import static model.Utils.msg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import controllers.FieldListener;
import controllers.MinMaxListener;
import controllers.RadioListener;
import controllers.SizeListener;
import controllers.ValueListener;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

/**
 * Popup used to prompt the user when he need to create an array reinitialize
 * the data when the popup is closed so it wont display the same data next
 * @author Dorian
 */
public class PopupTable {
	/**
	 * Dialog that will be displayed
	 */
	private Dialog<Object[]> dialog;
	
	/**
	 * Only instance of the popuptable
	 */
	private static PopupTable instance;

	/**
	 * Object to verify if we can validate the table
	 */
	private Check check;
	
	// Input fields
	/**
	 * Name of the table
	 */
	private TextField name;
	
	/**
	 * Description of the table
	 */
	private TextField description;
	
	/**
	 * Size of the table
	 */
	private TextField size;
	
	/**
	 * Value to be fill (same, crescent or descending)
	 */
	private TextField value;
	
	/**
	 * Minimum value if fill with random in range
	 */
	private TextField minVal;
	
	/**
	 * Maximum value if fill with random in range
	 */
	private TextField maxVal;
	
	/**
	 * Manual input of integer / character of the table
	 */
	private TextField valueTab;

	// all radio buttons to select way to fill table
	/**
	 * Crescent from value field number
	 */
	private RadioButton up;
	
	/**
	 * Descending from value field number
	 */
	private RadioButton down;
	
	/**
	 * All the same
	 */
	private RadioButton same;
	
	/**
	 * Random in range min max but sorted crescent
	 */
	private RadioButton randomUp;
	
	/**
	 * Random in range min max but sorted descending
	 */
	private RadioButton randomDown;
	
	/**
	 * Random in range min max
	 */
	private RadioButton random;
	
	/**
	 * Manual input of numbers / characters
	 */
	private RadioButton valueChoice;

	/**
	 * Listener of radio buttons
	 */
	private RadioListener radListener;

	/**
	 * Type of the popup
	 */
	private Label type;
	
	/**
	 * String defining the type
	 */
	private String typeStr;

	/**
	 * Create a new popup without parameters
	 * @param typeStr Type of the popup
	 */
	private PopupTable(String typeStr) {
		create(typeStr, "");
	}

	/**
	 * Modify table whith existing parameters
	 * @param typeStr Type of array
	 * @param namePassed name
	 */
	private PopupTable(String typeStr, String namePassed) {
		create(typeStr, namePassed);
	}

	/**
	 * Create the popup for the array
	 * @param typeStr  type of array
	 * @param namePassed name of the table
	 */
	private void create(String typeStr, String namePassed) {
		new JFXPanel();

		dialog = new Dialog<>();
		dialog.setTitle((namePassed + "").length() == 0 ? msg("PopupTable.title") : msg("PopupTable.modif.title"));

		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(new ButtonType(msg("PopupTable.cancel"), ButtonData.CANCEL_CLOSE));
		((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:blockly/algotouch/icons/icon_no_bg.png"));
		
		//
		// CREATE COMPONENTS
		//

		GridPane mainGrid = new GridPane();
		GridPane grid1 = new GridPane();
		GridPane grid2 = new GridPane();
		GridPane grid3 = new GridPane();
		GridPane grid4 = new GridPane();
		GridPane grid5 = new GridPane();
		GridPane grid6 = new GridPane();

		name = new TextField();
		description = new TextField();
		size = new TextField();
		value = new TextField();
		minVal = new TextField();
		maxVal = new TextField();
		valueTab = new TextField();

		up = new RadioButton(msg("PopupTable.up"));
		down = new RadioButton(msg("PopupTable.down"));
		same = new RadioButton(msg("PopupTable.same"));

		randomUp = new RadioButton(msg("PopupTable.random.up"));
		randomDown = new RadioButton(msg("PopupTable.random.down"));
		random = new RadioButton(msg("PopupTable.random.same"));

		valueChoice = new RadioButton(msg("PopupTable.do.it.yourself"));

		Node validateButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
		validateButton.setDisable(true);

		//
		// INIT LAYOUTS
		//

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(10);
		RowConstraints row2 = new RowConstraints();
		row1.setPercentHeight(10);
		RowConstraints row3 = new RowConstraints();
		row1.setPercentHeight(5);
		RowConstraints row4 = new RowConstraints();
		row1.setPercentHeight(5);
		RowConstraints row5 = new RowConstraints();
		row1.setPercentHeight(55);
		RowConstraints row6 = new RowConstraints();
		row1.setPercentHeight(15);

		mainGrid.getRowConstraints().addAll(row1, row2, row3, row4, row5, row6);

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(10);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(25);
		ColumnConstraints column3 = new ColumnConstraints();
		column3.setPercentWidth(30);
		ColumnConstraints column4 = new ColumnConstraints();
		column4.setPercentWidth(10);
		ColumnConstraints column5 = new ColumnConstraints();
		column5.setPercentWidth(25);

		grid1.getColumnConstraints().addAll(column1, column2, column3, column4, column5);

		column1 = new ColumnConstraints();
		column1.setPercentWidth(10);
		column2 = new ColumnConstraints();
		column2.setPercentWidth(90);

		grid2.getColumnConstraints().addAll(column1, column2);

		column1 = new ColumnConstraints();
		column1.setPercentWidth(10);
		column2 = new ColumnConstraints();
		column2.setPercentWidth(25);
		column3 = new ColumnConstraints();
		column3.setPercentWidth(65);

		grid3.getColumnConstraints().addAll(column1, column2, column3);

		column1 = new ColumnConstraints();
		column1.setPercentWidth(25);
		column2 = new ColumnConstraints();
		column2.setPercentWidth(75);

		grid4.getColumnConstraints().addAll(column1, column2);

		column1 = new ColumnConstraints();
		column1.setPercentWidth(10);
		column2 = new ColumnConstraints();
		column2.setHalignment(HPos.LEFT);
		column2.setPercentWidth(40);
		column3 = new ColumnConstraints();
		column3.setHalignment(HPos.LEFT);
		column3.setPercentWidth(25);
		column4 = new ColumnConstraints();
		column4.setPercentWidth(25);

		grid5.getColumnConstraints().addAll(column1, column2, column3, column4);

		column1 = new ColumnConstraints();
		column1.setPercentWidth(100);

		grid6.getColumnConstraints().addAll(column1);

		GridPane.setMargin(mainGrid, new Insets(5, 5, 5, 5));
		GridPane.setMargin(grid1, new Insets(5, 5, 5, 5));
		GridPane.setMargin(grid2, new Insets(5, 5, 5, 5));
		GridPane.setMargin(grid3, new Insets(5, 5, 5, 5));
		GridPane.setMargin(grid4, new Insets(5, 5, 5, 5));
		GridPane.setMargin(grid5, new Insets(5, 5, 5, 5));
		GridPane.setMargin(grid6, new Insets(5, 5, 5, 5));

		//
		// ADD COMPONENTS
		//
		
		type = new Label();
		if (typeStr.equals("integer")) {
			this.typeStr = "INTEGER";
			type.setText(msg("PopupTable.type.int"));
		} else if (typeStr.equals("char")) {
			this.typeStr = "CHAR";
			type.setText(msg("PopupTable.type.char"));
		}

		mainGrid.add(grid1, 0, 0);
		mainGrid.add(grid2, 0, 1);
		mainGrid.add(grid3, 0, 2);
		mainGrid.add(grid4, 0, 3);
		mainGrid.add(grid5, 0, 4);
		mainGrid.add(grid6, 0, 5);

		grid1.add(new Label(msg("PopupTable.name")), 0, 0);
		grid1.add(name, 1, 0);
		grid1.add(new Label(msg("PopupTable.type")), 3, 0);
		grid1.add(type, 4, 0);

		grid2.add(new Label(msg("PopupTable.description")), 0, 0);
		grid2.add(description, 1, 0);

		grid3.add(new Label(msg("PopupTable.size")), 0, 0);
		grid3.add(size, 1, 0);

		grid4.add(new Label(msg("PopupTable.choice")), 0, 0);

		grid5.add(same, 1, 0);
		grid5.add(up, 1, 1);
		grid5.add(value, 3, 1);
		grid5.add(down, 1, 2);
		grid5.add(new Label(msg("PopupTable.range")), 1, 3);
		grid5.add(minVal, 2, 3);
		grid5.add(maxVal, 3, 3);
		grid5.add(randomUp, 1, 4);
		grid5.add(randomDown, 1, 5);
		grid5.add(random, 1, 6);
		grid5.add(valueChoice, 1, 7);

		grid6.add(valueTab, 0, 0);

		//
		// ADD LISTENERS
		//

		ArrayList<RadioButton> list = new ArrayList<RadioButton>();
		list.add(valueChoice);
		list.add(same);
		list.add(random);
		list.add(randomDown);
		list.add(randomUp);
		list.add(down);
		list.add(up);

		HashMap<TextField, Boolean> map = new HashMap<TextField, Boolean>();
		map.put(value, false);
		map.put(valueTab, false);
		map.put(minVal, false);
		map.put(maxVal, false);
		map.put(size, false);

		this.check = new Check(map, validateButton);
		if (!namePassed.equals("")) {
			name.setText(namePassed);
			name.setEditable(false);
			check.setNameOK(true);
		}

		radListener = new RadioListener(list);
		same.setSelected(true);

		FieldListener fieldListener = new FieldListener(this.typeStr, map);
		valueTab.addEventFilter(KeyEvent.KEY_RELEASED, fieldListener);

		ValueListener valueListener = new ValueListener(this.typeStr, map);
		value.addEventFilter(KeyEvent.KEY_RELEASED, valueListener);

		MinMaxListener listenerMinMax = new MinMaxListener(minVal, maxVal, this.typeStr, map);
		minVal.addEventFilter(KeyEvent.KEY_RELEASED, listenerMinMax);
		maxVal.addEventFilter(KeyEvent.KEY_RELEASED, listenerMinMax);

		size.addEventFilter(KeyEvent.KEY_RELEASED, new SizeListener(map));

		name.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				TextField field = (TextField) event.getSource();
				if (field.getText().length() < 1 || Character.isDigit(field.getText().charAt(0))) {
					name.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
					check.setNameOK(false);
				}
				else {
					name.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
					check.setNameOK(true);
				}
				check.check();

			}

		});

		//
		// TERMINATE POPUP
		//

		dialog.getDialogPane().setContent(mainGrid);

		Platform.runLater(() -> name.requestFocus());

		dialog.setResultConverter(dialogButton -> {
			Object[] data = null;
			if (dialogButton == ButtonType.OK) {
				data = generateTable();
				cleanFields();
			}
			return data;
		});

		dialog.setOnCloseRequest(event -> dialog.close());
	}

	/**
	 * Clean all input fields and reset selections
	 */
	private void cleanFields() {
		name.setText("");
		description.setText("");
		size.setText("");
		value.setText("");
		minVal.setText("");
		maxVal.setText("");
		valueTab.setText("");

		radListener.handle(new ActionEvent(same, null));
	}

	/**
	 * Generate an array using the choices of the user
	 * 
	 * @return a string containing the name and the values of the array
	 */
	private Object[] generateTable() {
		Object[] response = new Object[3];
		Integer[] tab = null;
		if (typeStr.equals("INTEGER")) {

			if (!size.getText().equals(""))
				tab = new Integer[Integer.parseInt(size.getText())];

			if (same.isSelected()) {
				int val = Integer.parseInt(value.getText());
				for (int i = 0; i < tab.length; i++) {
					tab[i] = val;
				}
			} else if (up.isSelected()) {
				int val = Integer.parseInt(value.getText());
				for (int i = 0; i < tab.length; i++) {
					tab[i] = val;
					val++;
				}
			} else if (down.isSelected()) {
				int val = Integer.parseInt(value.getText());
				for (int i = 0; i < tab.length; i++) {
					tab[i] = val;
					val--;
				}
			} else if (random.isSelected()) {
				int min = Integer.parseInt(minVal.getText());
				int max = Integer.parseInt(maxVal.getText());
				tab = new Integer[tab.length];
				for (int i = 0; i < tab.length; i++) {

					tab[i] = (int) ((Math.random() * (max - min)) + min);
				}
			} else if (randomUp.isSelected()) {
				int min = Integer.parseInt(minVal.getText());
				int max = Integer.parseInt(maxVal.getText());
				int[] tab1 = new int[tab.length];
				for (int i = 0; i < tab.length; i++) {
					tab1[i] = (int) ((Math.random() * (max - min)) + min);
				}

				Arrays.sort(tab1);

				for (int i = 0; i < tab.length; i++) {
					tab[i] = tab1[i];
				}
			} else if (randomDown.isSelected()) {
				int min = Integer.parseInt(minVal.getText());
				int max = Integer.parseInt(maxVal.getText());
				int[] tab1 = new int[tab.length];
				for (int i = 0; i < tab.length; i++) {
					tab1[i] = (int) ((Math.random() * (max - min)) + min);
				}

				Arrays.sort(tab1);
				for (int i = 0; i < tab.length; i++) {
					tab[i] = tab1[tab1.length - i - 1];
				}
			} else if (valueChoice.isSelected()) {
				String[] res = valueTab.getText().split(" : ");
				tab = new Integer[res.length];
				for (int i = 0; i < tab.length; i++) {
					tab[i] = Integer.parseInt(res[i]);
				}
			}

		} else {

			if (!size.getText().equals(""))
				tab = new Integer[Integer.parseInt(size.getText())];

			if (same.isSelected()) {
				char val = value.getText().charAt(0);
				for (int i = 0; i < tab.length; i++) {
					tab[i] = (int) val;
				}
			} else if (up.isSelected()) {
				char val = value.getText().charAt(0);
				for (int i = 0; i < tab.length; i++) {
					tab[i] = (int) val;
					val++;
				}
			} else if (down.isSelected()) {
				char val = value.getText().charAt(0);
				for (int i = 0; i < tab.length; i++) {
					tab[i] = (int) val;
					val--;
				}
			} else if (random.isSelected()) {
				char min = minVal.getText().charAt(0);
				char max = maxVal.getText().charAt(0);

				for (int i = 0; i < tab.length; i++) {
					tab[i] = ((int) (Math.random() * (max - min)) + min);
				}
			} else if (randomUp.isSelected()) {
				char min = minVal.getText().charAt(0);
				char max = maxVal.getText().charAt(0);
				int[] tab1 = new int[tab.length];
				for (int i = 0; i < tab1.length; i++) {
					tab1[i] = (int) (min + (Math.random() * (max - min)));
				}

				Arrays.sort(tab1);
				for (int i = 0; i < tab.length; i++) {
					tab[i] = (int) tab1[i];
				}
			} else if (randomDown.isSelected()) {
				char min = minVal.getText().charAt(0);
				char max = maxVal.getText().charAt(0);
				int[] tab1 = new int[tab.length];
				for (int i = 0; i < tab1.length; i++) {
					tab1[i] = (int) (min + (Math.random() * (max - min)));
				}

				Arrays.sort(tab1);
				for (int i = 0; i < tab.length; i++) {
					tab[i] = (int) tab1[tab.length - i - 1];
				}
			} else if (valueChoice.isSelected()) {
				String[] res = valueTab.getText().split(" : ");
				tab = new Integer[res.length];
				for (int i = 0; i < tab.length; i++) {
					tab[i] = (int) res[i].charAt(0);
				}
			}

		}

		response[0] = name.getText();
		response[1] = description.getText();
		response[2] = tab;

		return response;
	}

	/**
	 * Get what the user write inside the text field
	 * @return what the user write or else null
	 */
	private Object[] getInput() {
		return dialog.showAndWait().orElse(null);
	}

	/**
	 * Get the instance of the popup table
	 * @return the only instance
	 */
	public static PopupTable getInstance() {
		return instance;
	}

	/**
	 * Display the popup table with parameters
	 * @param type The type of the popup
	 * @param namePassed The name passed for the popup
	 * @return the name of the array entered and the values
	 */
	public static Object[] showPopupTable(String type, String namePassed) {
		instance = new PopupTable(type, namePassed);
		return instance.getInput();
	}

	/**
	 * Display the popup table
	 * @param type the type of the popup
	 * @return the name of the array entered and the values
	 */
	public static Object[] showPopupTable(String type) {
		instance = new PopupTable(type);
		return instance.getInput();
	}

	/**
	 * Class used to handle the name and to communicate with other listener
	 * and check if table could be generated with current parameters
	 * @author dorian
	 *
	 */
	private class Check {
		/**
		 * Check if name is valid
		 */
		boolean nameOK;
		
		/**
		 * All listened fields with their valid value (true or false)
		 */
		HashMap<TextField, Boolean> map;
		
		/**
		 * OK button to disable or enable if everything OK
		 */
		Node button;
		
		/**
		 * true if everything ok false else
		 */
		boolean OK;

		/**
		 * Create the Check handler 
		 * @param map the map of fields listened
		 * @param button the OK button
		 */
		Check(HashMap<TextField, Boolean> map, Node button) {
			nameOK = false;
			this.map = map;
			this.button = button;
			OK = false;
		}

		/**
		 * Set if the name is correct or not
		 * @param ok true if the name is correct, false
		 */
		public void setNameOK(boolean ok) {
			this.nameOK = ok;
		}

		/**
		 * Set the ok button to disable if the informations entered is not correct
		 */
		public void check() {
			OK = true;
			if (!nameOK)
				OK = false;
			if (OK && !map.get(size))
				OK = false;
			if (OK)
				if (up.isSelected() || down.isSelected() || same.isSelected()) {
					if (!map.get(value)) {
						OK = false;
					}
				} else if (randomUp.isSelected() || randomDown.isSelected() || random.isSelected()) {
					if (!map.get(minVal) || !map.get(maxVal)) {
						OK = false;
					}
				} else if (valueChoice.isSelected()) {
					if (!map.get(valueTab)) {
						OK = false;
					} else
						OK = true;
				}

			if (!OK)
				button.setDisable(true);
			else
				button.setDisable(false);
		}
	}

	/**
	 * Get the check
	 */
	public void check() {
		check.check();
	}
}
