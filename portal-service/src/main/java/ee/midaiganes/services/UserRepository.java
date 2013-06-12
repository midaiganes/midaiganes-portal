package ee.midaiganes.services;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.beans.RootApplicationContext;
import ee.midaiganes.model.User;
import ee.midaiganes.services.SingleVmPool.Cache;
import ee.midaiganes.services.exceptions.DuplicateUsernameException;
import ee.midaiganes.services.rowmapper.UserRowMapper;
import ee.midaiganes.services.statementcreator.AddUserPreparedStatementCreator;

@Component(value = RootApplicationContext.USER_REPOSITORY)
public class UserRepository {
	private static UserRepository instance;

	@Resource(name = PortalConfig.PORTAL_JDBC_TEMPLATE)
	private JdbcTemplate jdbcTemplate;

	private static final String SELECT_USER_FROM_USER = "SELECT id, username FROM User";
	private static final String GET_USER_BY_USERID = SELECT_USER_FROM_USER + " WHERE id = ?";
	private static final String GET_USER_BY_USERNAME_PASSWORD = SELECT_USER_FROM_USER + " WHERE username = ? AND password = ?";
	private static final String GET_USER_BY_USERNAME = SELECT_USER_FROM_USER + " WHERE username = ?";
	private static final String QRY_GET_USERS_COUNT = "SELECT COUNT(1) FROM User";
	private static final String QRY_GET_USERS_ORDER_BY_ID_ASC_LIMIT = SELECT_USER_FROM_USER + " ORDER BY id ASC LIMIT ?, ?";

	private final UserRowMapper userRowMapper;
	private final Cache cache;

	public static UserRepository getInstance() {
		return instance;
	}

	public static void setInstance(UserRepository instance) {
		UserRepository.instance = instance;
	}

	public UserRepository() {
		cache = SingleVmPool.getCache(UserRepository.class.getName());
		userRowMapper = new UserRowMapper();
	}

	public long getUsersCount() {
		return jdbcTemplate.queryForLong(QRY_GET_USERS_COUNT);
	}

	public List<User> getUsers(long start, long count) {
		return jdbcTemplate.query(QRY_GET_USERS_ORDER_BY_ID_ASC_LIMIT, userRowMapper, start, count);
	}

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
			jdbcTemplate.update(new AddUserPreparedStatementCreator(username, password), keyHolder);
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

	public User getUser(String username) {
		List<User> users = jdbcTemplate.query(GET_USER_BY_USERNAME, userRowMapper, username);
		User user = users.isEmpty() ? null : users.get(0);
		if (user != null) {
			cache.put(Long.toString(user.getId()), user);
		}
		return user;
	}
}
