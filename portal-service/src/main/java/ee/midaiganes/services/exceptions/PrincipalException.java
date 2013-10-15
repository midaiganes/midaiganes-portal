package ee.midaiganes.services.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.midaiganes.model.PortalResource;

public class PrincipalException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(PrincipalException.class);

	public PrincipalException() {
		super(null, null, false, log.isDebugEnabled());
	}

	public PrincipalException(String msg) {
		super(msg, null, false, log.isDebugEnabled());
	}

	public PrincipalException(long userId, PortalResource resource, String action) {
		this("User " + userId + " has no permission to '" + resource.getResource() + "'@'" + resource.getId() + "':'" + action + "'");
	}

	public PrincipalException(Exception e) {
		super(e);
	}
}
