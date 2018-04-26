package view.component;

import static model.Utils.msg;

import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

/**
 * Popup used to let user choose his execution parameters
 * the data when the popup is closed so it wont display the same data next
 * @author Dorian
 */
public class PopupParams {
	/**
	 * Dialog that will be displayed
	 */
	private Dialog<Object[]> dialog;
	
	/**
	 * Only instance of the popupparams
	 */
	private static PopupParams instance;
	
	/**
	 * Save the last valid configuration
	 */
	private static boolean[] save = new boolean[3];;
	
	
	/**
	 * Switch between run and step by step
	 */
	private ToggleSwitch run;
	
	/**
	 * Switch between deep mode and surface mode
	 */
	private ToggleSwitch deep;
	
	/**
	 * Switch between automatic mode and manual mode
	 */
	private ToggleSwitch auto;
	
	/**
	 * Field to enter timeout
	 */
	private TextField custom;
	
	/**
	 * Label to display current selected mode
	 */
	private Label deepLab;
	
	/**
	 * Label to display current selected mode
	 */
	private Label runLab;
	

	/**
	 * Create a new popup without parameters
	 */
	private PopupParams() {
		new JFXPanel();

		dialog = new Dialog<>();
		dialog.setTitle(msg("PopupParams.title"));

		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(new ButtonType(msg("PopupTable.cancel"), ButtonData.CANCEL_CLOSE));
		((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:blockly/algotouch/icons/icon_no_bg.png"));
		
		//
		// CREATE COMPONENTS
		//

		GridPane mainGrid = new GridPane();
		deep = new ToggleSwitch(msg("PopupParams.deep"), msg("PopupParams.surface"));
		auto = new ToggleSwitch("Auto", msg("PopupParams.manual"));
		run = new ToggleSwitch(msg("PopupParams.run"), msg("PopupParams.steps"));
		run.switchOnProperty().set(true);
		
		custom = new TextField();
		custom.setDisable(true);
		custom.setPromptText(msg("PopupParams.holder"));
		
		deepLab = new Label(msg("PopupParams.surface.lab"));
		runLab = new Label(msg("PopupParams.run.lab"));

		//
		// INIT LAYOUTS
		//

		RowConstraints row1 = new RowConstraints();
		row1.setPrefHeight(30);
		RowConstraints row2 = new RowConstraints();
		row2.setPrefHeight(2);
		RowConstraints row3 = new RowConstraints();
		row3.setPrefHeight(30);
		RowConstraints row4 = new RowConstraints();
		row4.setPrefHeight(2);
		RowConstraints row5 = new RowConstraints();
		row5.setPrefHeight(30);

		mainGrid.getRowConstraints().addAll(row1, row2, row3, row4, row5);

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(50);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(50);

		mainGrid.getColumnConstraints().addAll(column1, column2);
		
		GridPane.setMargin(custom, new Insets(0 , 0, 0, 5));
		GridPane.setMargin(deepLab, new Insets(0 , 0, 0, 5));
		GridPane.setMargin(runLab, new Insets(0 , 0, 0, 5));
		

		//
		// ADD COMPONENTS
		//
		
		mainGrid.add(run, 0, 0);
		mainGrid.add(runLab, 1, 0);
		mainGrid.add(deep, 0, 2);
		mainGrid.add(deepLab, 1, 2);		
		mainGrid.add(auto, 0, 4);
		mainGrid.add(custom, 1, 4);

		//
		// ADD LISTENERS
		//

		deep.setListener((a, b, value) -> {
			if(value) deepLab.setText(msg("PopupParams.deep.lab"));
			else deepLab.setText(msg("PopupParams.surface.lab"));
		});
		
		run.setListener((a, b, value) -> {
			if(value) runLab.setText(msg("PopupParams.run.lab"));
			else runLab.setText(msg("PopupParams.step.lab"));
			handling(run.switchOnProperty().get());
		});
		
		auto.setListener((a, b, value) -> {
			if(!value) custom.setDisable(true);
			else custom.setDisable(false);
			handling(!value);
		});
		
		custom.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				handling(false);
			}
		});
		
		//
		// TERMINATE POPUP
		//

		dialog.getDialogPane().setContent(mainGrid);

		dialog.setResultConverter(dialogButton -> {
			Object[] data = null;
			if (dialogButton == ButtonType.OK) {
				data = new Object[4];
				data[0] = run.switchOnProperty().get();
				data[1] = deep.switchOnProperty().get();
				data[2] = auto.switchOnProperty().get();
				int val = 0;
				
				try {
					val = Integer.parseInt(custom.getText());
				} catch(Exception e) {
					if(auto.switchOnProperty().get()) val = 500;
				}
				if(auto.switchOnProperty().get() && val == 0) val = 500;
				data[3] = val;
				
				save[0] = run.switchOnProperty().get();
				save[1] = deep.switchOnProperty().get();
				save[2] = auto.switchOnProperty().get();
			}
			else {
				run.switchOnProperty().set(save[0]);
				deep.switchOnProperty().set(save[1]);
				auto.switchOnProperty().set(save[2]);
			}
			return data;
		});

		dialog.setOnCloseRequest(event -> dialog.close());
	}
	
	/**
	 * Test if custom fit to the needed parameters
	 * @param ignore
	 */
	private void handling(boolean ignore) {
		if(!ignore) {
			if(!run.switchOnProperty().get()) {
				String text = custom.getText();
				try {
					Integer.parseInt(text);
					custom.setStyle("-fx-border-color: green ; -fx-border-width: 1px ;");
					dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
				}
				catch (Exception e) {
					if(auto.switchOnProperty().get()) {
						dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
						custom.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
					}
				}
			}
			else dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
		}
		else dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(false);
	}


	/**
	 * Get what the user write inside the text field
	 * @return what the user write or else null
	 */
	private Object[] getInput() {
		return dialog.showAndWait().orElse(null);
	}

	/**
	 * Get the instance of the popup params
	 * @return the only instance
	 */
	public static PopupParams getInstance() {
		return instance;
	}

	/**
	 * Display the popup params
	 * @return the name of the array entered and the values
	 */
	public static Object[] showPopupParams() {
		if(instance == null) instance = new PopupParams();
		return instance.getInput();
	}
}
