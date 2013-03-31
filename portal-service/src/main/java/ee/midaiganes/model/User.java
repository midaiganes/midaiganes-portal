package ee.midaiganes.model;

import java.io.Serializable;

public class User implements Serializable, PortalResource {
	private static final long serialVersionUID = 1L;

	private long id;
	private String username;
	private String password;

	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDefaultUser() {
		return id == DefaultUser.DEFAULT_USER_ID;
	}

	@Override
	public String getResource() {
		return User.class.getName();
	}

	@Override
	public int hashCode() {
		return ((int) id) + username.hashCode() + password.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			User u = (User) obj;
			return id == u.id && username.equals(u.username) && password.equals(u.password);
		}
		return false;
	}

}
