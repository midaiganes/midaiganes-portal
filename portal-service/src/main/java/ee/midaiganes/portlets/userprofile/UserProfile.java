package ee.midaiganes.portlets.userprofile;

import java.io.Serializable;

import ee.midaiganes.model.User;

public class UserProfile implements Serializable {
	private static final long serialVersionUID = 1L;
	private final boolean isCurrentUser;
	private final User user;

	public UserProfile(User user, boolean isCurrentUser) {
		this.isCurrentUser = isCurrentUser;
		this.user = user;
	}

	public boolean isCurrentUser() {
		return isCurrentUser;
	}

	public String getUsername() {
		return user.getUsername();
	}

	public String getPictureUrl() {
		return null;
	}
}
