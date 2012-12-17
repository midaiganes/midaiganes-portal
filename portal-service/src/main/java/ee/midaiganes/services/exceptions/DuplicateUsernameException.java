package ee.midaiganes.services.exceptions;

public class DuplicateUsernameException extends Exception {
	private static final long serialVersionUID = 1L;

	public DuplicateUsernameException(Throwable cause) {
		super(cause);
	}

}
