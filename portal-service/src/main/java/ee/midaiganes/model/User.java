package ee.midaiganes.model;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String username;
	private String password;

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
}
