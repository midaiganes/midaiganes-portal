package ee.midaiganes.services.exceptions;

public class ResourceActionNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ResourceActionNotFoundException(final String msg) {
		super(msg);
	}

	public ResourceActionNotFoundException(final long resourceId, final String action) {
		super("Invalid resourceId " + resourceId + " or action '" + action + "'");
	}
}