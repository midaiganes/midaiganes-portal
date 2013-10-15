package ee.midaiganes.portlets.login;

import java.io.Serializable;

import ee.midaiganes.util.StringUtil;

public class LoginData implements Serializable {
	private static final long serialVersionUID = 1L;

	private String username;
	private String password;

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

	@Override
	public String toString() {
		return "LoginData [username='" + username + "', password='" + (password == null ? null : StringUtil.repeat("*", password.length())) + "']";
	}
}
