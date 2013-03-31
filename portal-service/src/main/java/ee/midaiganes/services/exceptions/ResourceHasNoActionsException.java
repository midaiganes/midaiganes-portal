package ee.midaiganes.services.exceptions;

public class ResourceHasNoActionsException extends ResourceActionNotFoundException {
	private static final long serialVersionUID = 1L;

	public ResourceHasNoActionsException(final long resourceId, final String action) {
		super(resourceId, action);
	}

}
