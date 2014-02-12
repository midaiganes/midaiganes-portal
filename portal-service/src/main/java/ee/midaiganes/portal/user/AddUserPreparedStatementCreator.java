package ee.midaiganes.portal.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;

import ee.midaiganes.util.StringPool;

public final class AddUserPreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
	private static final String ADD_USER = "INSERT INTO User(username, password) VALUES(?, ?)";
	private static final String[] ID_ARRAY = new String[] { StringPool.ID };
	private final String username;
	private final String password;

	public AddUserPreparedStatementCreator(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public String getSql() {
		return ADD_USER;
	}

	@Override
	public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		PreparedStatement ps = con.prepareStatement(ADD_USER, ID_ARRAY);
		ps.setString(1, username);
		ps.setString(2, password);
		return ps;
	}
}