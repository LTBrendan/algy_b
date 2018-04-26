package model;

public class InvalidNameException extends Exception {
	private static final long serialVersionUID = 6059203686450781010L;

	public InvalidNameException(String message) {
		super(message);
	}
}
