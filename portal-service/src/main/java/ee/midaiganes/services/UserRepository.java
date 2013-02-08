package ee.midaiganes.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ee.midaiganes.beans.RootApplicationContext;
import ee.midaiganes.model.User;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.exceptions.DuplicateUsernameException;
import ee.midaiganes.services.rowmapper.UserRowMapper;
import ee.midaiganes.util.StringPool;

@Component(value = RootApplicationContext.USER_REPOSITORY)
public class UserRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	private static final String SELECT_USER_FROM_USER = "SELECT id, username FROM User";
	private static final String GET_USER_BY_USERID = SELECT_USER_FROM_USER + " WHERE id = ?";
	private static final String GET_USER_BY_USERNAME_PASSWORD = SELECT_USER_FROM_USER + " WHERE username = ? AND password = ?";
	private static final String ADD_USER = "INSERT INTO User(username, password) VALUES(?, ?)";
	private static final String[] ID_ARRAY = new String[] { StringPool.ID };

	private final Cache cache = SingleVmPool.getCache(UserRepository.class.getName());

	private static final UserRowMapper userRowMapper = new UserRowMapper();

	public User getUser(long userid) {
		String cacheKey = Long.toString(userid);
		User user = cache.get(cacheKey);
		if (user == null) {
			List<User> users = jdbcTemplate.query(GET_USER_BY_USERID, userRowMapper, userid);
			user = users.isEmpty() ? null : users.get(0);
			if (user != null) {
				cache.put(cacheKey, user);
			}
		}
		return user;
	}

	public long addUser(final String username, final String password) throws DuplicateUsernameException {
		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(ADD_USER, ID_ARRAY);
					ps.setString(1, username);
					ps.setString(2, password);
					return ps;
				}
			}, keyHolder);
			return keyHolder.getKey().longValue();
		} catch (DuplicateKeyException e) {
			throw new DuplicateUsernameException(e);
		}
	}

	public User getUser(String username, String password) {
		List<User> users = jdbcTemplate.query(GET_USER_BY_USERNAME_PASSWORD, userRowMapper, username, password);
		User user = users.isEmpty() ? null : users.get(0);
		if (user != null) {
			cache.put(Long.toString(user.getId()), user);
		}
		return user;
	}
}
