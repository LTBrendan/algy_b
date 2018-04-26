package view.main;

import static model.Utils.msg;

import java.io.File;

import com.sun.javafx.application.PlatformImpl;

import controllers.PromptController;
import controllers.WatchListener;
import controllers.jslink.Bridge;
import controllers.jslink.Switch;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Program;
import model.Utils;
import netscape.javascript.JSObject;
import view.component.Popup;

/**
 * Application ALGY UI
 * @author dorian
 */
public class App extends Application {

	/**
	 * Display animation at the start of the program
	 */
	private BorderPane displayPanel;

	/**
	 * Panel containing Blockly
	 */
	private BorderPane mainPanel;

	/**
	 * Set a webview of blockly
	 */
	private WebView mainBrowser;

	/**
	 * Handle the webview actions for blockly
	 */
	private WebEngine mainEngine;

	/**
	 * Set a webview of documentation
	 */
	private WebView docBrowser;

	/**
	 *Hhandle webview actions for documentation
	 */
	private WebEngine docEngine;

	/**
	 * Enable switch between blockly and documentation
	 */
	private Switch switcher;

	/**
	 * Handle javascript requests
	 */
	private Bridge bridge;

	/**
	 * Display variables states
	 */
	private WatchPanel watchPanel;

	/**
	 * Used to execute blocks arrangement
	 */
	private Program program;

	/**
	 * Stack of components to display
	 */
	private StackPane stack;

	/**
	 * tage off application
	 */
	private Stage stage;

	/**
	 * Start the application
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;

		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});

		ImageView loading = new ImageView("file:blockly/algotouch/icons/logo_start.gif");

		displayPanel = new BorderPane();
		displayPanel.setBackground(Background.EMPTY);
		displayPanel.setCenter(loading);

		FadeTransition fade = new FadeTransition(Duration.millis(1000), loading);
		fade.setDelay(Duration.millis(1750));
		fade.setFromValue(1.0);
		fade.setToValue(0.0);
		fade.play();
		fade.setOnFinished(e -> displayBlockly());

		Scene scene = new Scene(displayPanel);

		primaryStage.getIcons().add(new Image("file:blockly/algotouch/icons/icon_bg.png"));
		primaryStage.setTitle("ALGY");
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();

		this.switcher = new Switch(this);
			
		docBrowser = new WebView();
		docEngine = docBrowser.getEngine();
		docEngine.setJavaScriptEnabled(true);
		
		loadMain();
		loadDoc();
	}

	/**
	 * Load blockly web view
	 */
	private void loadMain() {
		this.watchPanel = new WatchPanel();
		this.program = new Program();
		program.setApplication(this);
		program.setListener(new WatchListener(watchPanel));
		bridge = new Bridge(this.program, this, watchPanel);

		mainBrowser = new WebView();
		mainBrowser.setContextMenuEnabled(false);
		mainEngine = mainBrowser.getEngine();
		mainEngine.setJavaScriptEnabled(true);

		// Chargement du blockly
		mainEngine.load(asUrl("blockly/algotouch/index.html?lang=" + Utils.getLang().toLowerCase()));
		

		// ajoute le pont entre js et java
		mainEngine.getLoadWorker().stateProperty().addListener((obs, old, next) -> {
			if (next == State.SUCCEEDED) {
				JSObject window = (JSObject) mainEngine.executeScript("window");
				window.setMember("controller", bridge);
				window.setMember("switcher", switcher);
			}
		});

		mainEngine.setConfirmHandler(data -> Popup.showConfirmPopup(msg("App.confirm"), data));
		mainEngine.setPromptHandler(new PromptController(App.this.program));
		mainEngine.setOnAlert((data) -> Popup.showMessagePopup("Message", data.getData()));
		
		mainPanel = new BorderPane();
		
		stack = new StackPane();
		stack.getChildren().add(mainBrowser);
		mainPanel.setCenter(stack);
		mainPanel.setRight(watchPanel);
	}
	
	/**
	 * Add a gif at bottom left of the frame
	 */
	public void addExecutionIcon() {
		PlatformImpl.runLater(() -> {
			ImageView gif = new ImageView("file:blockly/algotouch/icons/logo_red_3D_small.gif");
			StackPane.setMargin(gif, new Insets(5, 5, 5, 5));
			StackPane.setAlignment(gif, Pos.BOTTOM_LEFT);
			stack.getChildren().add(gif);
		});
	}
	
	
	/**
	 * Remove the gif at bottom left of the frame
	 */
	public void removeExecutionIcon() {
		PlatformImpl.runLater(() -> stack.getChildren().remove(stack.getChildren().size()-1));
	}

	/**
	 * Load Documentation web view
	 */
	public void loadDoc() {
		
		docEngine.load(asUrl("blockly/algotouch/documentation/" + Utils.getLang() + "/index.html"));

		docEngine.getLoadWorker().stateProperty().addListener((obs, old, next) -> {
			if (next == State.SUCCEEDED) {
				JSObject window = (JSObject) docEngine.executeScript("window");
				window.setMember("switcher", switcher);
			}
		});
		
	}

	/**
	 * Get a file url
	 * @param path Path of the file
	 * @return The URL
	 */
	private static String asUrl(String path) {
		path = path.replace("file:", "").replace("/", File.separator);
		return "file:///" + System.getProperty("user.dir")
				+ (path.startsWith(File.separator) ? path : File.separator + path);
	}

	/**
	 * display blockly view
	 */
	public void displayBlockly() {
		displayPanel.setCenter(mainPanel);
	}

	/**
	 * Display documentation view
	 */
	public void displayDocumentation() {
		displayPanel.setCenter(docBrowser);
	}

	/**
	 * Launch our application
	 * @param args arguments of launch
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Get the web engine for blockly
	 * @return the blockly web engine
	 */
	public WebEngine getMainEngine() {
		return mainEngine;
	}
	
	/**
	 * Set the frame title
	 * @param newTitle the new title of the frame
	 */
	public void setTitle(String newTitle) {
		stage.setTitle(newTitle);
	}

}
