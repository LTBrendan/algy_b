package control;

import control.Controller;
import model.Algorithm;
import view.AlgoTouchFrame;

public class Launcher {
	public static void main(String[] args) {
		Algorithm algorithm = new Algorithm();
		Controller controller = new Controller(algorithm);
		AlgoTouchFrame frame = new AlgoTouchFrame(controller, 1120, 720);
		controller.setView(frame);
		frame.setLocation(100, 50);
		frame.setSize(1280, 720);
		frame.setVisible(true);
	}
}
