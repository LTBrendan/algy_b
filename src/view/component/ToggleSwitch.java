package view.component;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;


public class ToggleSwitch extends HBox {
	
	private final Label label = new Label();
	private final Button button = new Button();
	
	private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(false);
	public SimpleBooleanProperty switchOnProperty() { return switchedOn; }
	
	private void init(String init) {
		
		label.setText(init);
		
		getChildren().addAll(label, button);	
		button.setOnAction((e) -> {
			switchedOn.set(!switchedOn.get());
		});
		label.setOnMouseClicked((e) -> {
			switchedOn.set(!switchedOn.get());
		});
		setStyle();
		bindProperties();
	}
	
	private void setStyle() {
		//Default Width
		setWidth(80);
		label.setAlignment(Pos.CENTER);
		setStyle("-fx-background-color: #B22222; -fx-text-fill: white; -fx-background-radius: 4;");
		setAlignment(Pos.CENTER_LEFT);
		label.setTextFill(Color.WHITE);
	}
	
	private void bindProperties() {
		label.prefWidthProperty().bind(widthProperty().divide(2));
		label.prefHeightProperty().bind(heightProperty());
		button.prefWidthProperty().bind(widthProperty().divide(2));
		button.prefHeightProperty().bind(heightProperty());
	}
	
	public ToggleSwitch(String first, String second) {
		init(second);
		switchedOn.addListener((a,b,c) -> {
			if (c) {
                label.setText(first);
                setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-background-radius: 4;");
                label.toFront();
                label.setTextFill(Color.WHITE);
            }
            else {
            	label.setText(second);
            	setStyle("-fx-background-color: #B22222; -fx-margin: 5px; -fx-text-fill: white; -fx-background-radius: 4;");
                button.toFront();
                label.setTextFill(Color.WHITE);
            }
		});
	}
	
	public void setListener(ChangeListener<Boolean> listener) {
		switchedOn.addListener(listener);
	}
}