package view.component;

import static model.Utils.msg;

import org.omg.CORBA.BooleanHolder;

import com.sun.javafx.application.PlatformImpl;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * The popup that will be displayed when the user is prompted or when an alert
 * has to be displayed
 * @author Daphnis
 */
public class Popup {

	/**
	 * Dialog that will be displayed
	 */
	private Dialog<String> dialog;

	/**
	 * Create a new Popup with the specified title, specified text
	 * @param title the title of the popup
	 * @param text the text of the popup
	 * @param input if the user has to be prompted
	 */
	private Popup(String title, String text, Boolean input) {
		dialog = new Dialog<String>();
		dialog.setTitle(title);
		((Stage) dialog.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:blockly/algotouch/icons/icon_no_bg.png"));

		if (input == null)
			dialog.setContentText(text);
		else {
			
			dialog.getDialogPane().getButtonTypes().add(new ButtonType(msg("Popup.cancel"), ButtonData.CANCEL_CLOSE));
			
			if (input) {
				GridPane pane = new GridPane();
				TextField entry = new TextField();
				entry.setPromptText(msg("Popup.answer"));

				pane.add(new Label(text), 0, 0);
				pane.add(entry, 0, 1);

				Node validateButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
				validateButton.setDisable(true);

				entry.textProperty().addListener((observable, oldValue, newValue) -> {
					validateButton.setDisable(newValue.trim().isEmpty());
				});

				dialog.getDialogPane().setContent(pane);

				Platform.runLater(() -> entry.requestFocus());

				dialog.setResultConverter(dialogButton -> {
					String data = null;
					if (dialogButton == ButtonType.OK) {
						data = entry.getText();
					}
					return data;
				});

			} else {
				dialog.setContentText(text);
				dialog.setResultConverter(diag -> String.valueOf(diag == ButtonType.OK));
			}
		}

		dialog.setOnCloseRequest(event -> dialog.close());

	}

	/**
	 * Get what the user write inside the text field
	 * @return what the user write or else null
	 */
	private String getInput() {
		return dialog.showAndWait().orElse("");
	}

	/**
	 * Display the dialog
	 */
	private void show() {
		dialog.show();
	}

	private boolean confirm() {
		return Boolean.parseBoolean(dialog.showAndWait().orElse("false"));
	}

	/**
	 * Show a popup with the specified title an message
	 * @param title title of the popup
	 * @param message message of the popup
	 */
	public static void showMessagePopup(String title, String message) {
		PlatformImpl.runLater(() -> new Popup(title, message, null).show());
	}

	/**
	 * Show a popup with a text field
	 * @param title title of the popup
	 * @param message message of the popup
	 * @return the input of the user
	 */
	public static String showInputPopup(String title, String message) {

		final StringBuilder input = new StringBuilder();

		PlatformImpl.runAndWait(() -> {
			Popup popup = new Popup(title, message, true);
			input.append(popup.getInput());
		});

		return input.length() == 0 ? null : input.toString();
	}

	/**
	 * Show a popup with for confirmation
	 * @param title title of the popup
	 * @param message message of the popup
	 * @return true if ok, false otherwise
	 */
	public static boolean showConfirmPopup(String title, String message) {

		BooleanHolder bool = new BooleanHolder();

		PlatformImpl.runAndWait(() -> {
			Popup popup = new Popup(title, message, false);
			bool.value = popup.confirm();
		});

		return bool.value;
	}

}
