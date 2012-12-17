package ee.midaiganes.services.exceptions;

public class IllegalFriendlyUrlException extends Exception {
	private static final long serialVersionUID = 1L;

	public IllegalFriendlyUrlException(String friendlyUrl) {
		super("Illegal friendly url: " + friendlyUrl);
	}
}
