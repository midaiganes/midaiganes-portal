package ee.midaiganes.portal.user;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.google.common.base.Preconditions;

import ee.midaiganes.services.exceptions.DuplicateUsernameException;
import ee.midaiganes.util.StringUtil;

public class UserDao {
    private static final String SELECT_USER_FROM_USER = "SELECT id, username FROM User";
    private static final String GET_USER_BY_USERID = SELECT_USER_FROM_USER + " WHERE id = ?";
    private static final String GET_USERS_BY_USERIDS = SELECT_USER_FROM_USER + " WHERE id IN (";
    private static final String GET_USER_BY_USERNAME_PASSWORD = SELECT_USER_FROM_USER + " WHERE username = ? AND password = ?";
    private static final String GET_USER_BY_USERNAME = SELECT_USER_FROM_USER + " WHERE username = ?";
    private static final String QRY_GET_USERS_COUNT = "SELECT COUNT(1) FROM User";
    private static final String QRY_GET_USERS_ORDER_BY_ID_ASC_LIMIT = SELECT_USER_FROM_USER + " ORDER BY id ASC LIMIT ?, ?";
    private static final UserRowMapper userRowMapper = new UserRowMapper();
    private final JdbcTemplate jdbcTemplate;

    @Inject
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long getUsersCount() {
        return jdbcTemplate.queryForObject(QRY_GET_USERS_COUNT, Long.class).longValue();
    }

    public List<User> getUsers(long start, long count) {
        return jdbcTemplate.query(QRY_GET_USERS_ORDER_BY_ID_ASC_LIMIT, userRowMapper, Long.valueOf(start), Long.valueOf(count));
    }

    @Nullable
    public User getUser(long userid) {
        List<User> list = jdbcTemplate.query(GET_USER_BY_USERID, userRowMapper, Long.valueOf(userid));
        return list.isEmpty() ? null : list.get(0);
    }

    public List<User> getUsers(Long[] userIds) {
        return jdbcTemplate.query(GET_USERS_BY_USERIDS + StringUtil.repeat("?", ",", userIds.length) + ")", userIds, userRowMapper);
    }

    public long addUser(@Nonnull final String username, @Nonnull final String password) throws DuplicateUsernameException {
        Preconditions.checkNotNull(username, "Username is null.");
        Preconditions.checkNotNull(password, "Password is null.");
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new AddUserPreparedStatementCreator(username, password), keyHolder);
            return keyHolder.getKey().longValue();
        } catch (DuplicateKeyException e) {
            throw new DuplicateUsernameException(e);
        }
    }

    @Nullable
    public User getUser(@Nonnull String username, @Nonnull String password) {
        Preconditions.checkNotNull(username, "Username is null.");
        Preconditions.checkNotNull(password, "Password is null.");
        List<User> list = jdbcTemplate.query(GET_USER_BY_USERNAME_PASSWORD, userRowMapper, username, password);
        return list.isEmpty() ? null : list.get(0);
    }

    @Nullable
    public User getUser(@Nonnull String username) {
        Preconditions.checkNotNull(username);
        List<User> list = jdbcTemplate.query(GET_USER_BY_USERNAME, userRowMapper, username);
        return list.isEmpty() ? null : list.get(0);
    }
}
