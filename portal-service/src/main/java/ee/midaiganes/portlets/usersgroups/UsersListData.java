package ee.midaiganes.portlets.usersgroups;

import java.io.Serializable;
import java.util.List;

import ee.midaiganes.portal.group.Group;
import ee.midaiganes.portal.user.User;

public class UsersListData implements Serializable {
	private static final long serialVersionUID = 1L;
	private final User user;
	private final List<Group> userGroups;
	private final List<Group> notUserGroups;

	public UsersListData(User user, List<Group> userGroups, List<Group> notUserGroups) {
		this.user = user;
		this.userGroups = userGroups;
		this.notUserGroups = notUserGroups;
	}

	public User getUser() {
		return user;
	}

	public List<Group> getUserGroups() {
		return userGroups;
	}

	public List<Group> getNotUserGroups() {
		return notUserGroups;
	}
}
