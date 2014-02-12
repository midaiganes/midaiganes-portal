package ee.midaiganes.portal.group;

import java.io.Serializable;

import ee.midaiganes.model.PortalResource;

public class Group implements Serializable, PortalResource {
	private static final long serialVersionUID = 1L;
	private final long id;
	private final String name;
	private final boolean userGroup;

	public Group(long id, String name, boolean userGroup) {
		this.id = id;
		this.name = name;
		this.userGroup = userGroup;
	}

	@Override
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isUserGroup() {
		return userGroup;
	}

	@Override
	public String getResource() {
		return Group.class.getName();
	}
}
