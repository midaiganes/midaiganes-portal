package ee.midaiganes.model;

import java.io.Serializable;

public final class ResourceActionPermission implements Serializable {
	private static final long serialVersionUID = 1L;
	private final long resourceId;
	private final String action;
	private final long permission;

	public ResourceActionPermission(final long resourceId, final String action, final long permission) {
		this.resourceId = resourceId;
		this.action = action;
		this.permission = permission;
	}

	public final long getResourceId() {
		return resourceId;
	}

	public final String getAction() {
		return action;
	}

	public final long getPermission() {
		return permission;
	}
}
