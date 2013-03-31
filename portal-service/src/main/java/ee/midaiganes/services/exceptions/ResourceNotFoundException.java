package ee.midaiganes.services.exceptions;

public final class ResourceNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(final String msg) {
		super(msg);
	}
}