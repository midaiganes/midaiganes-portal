package ee.midaiganes.portal.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import ee.midaiganes.beans.PortalConfig;
import ee.midaiganes.cache.Element;
import ee.midaiganes.cache.SingleVmCache;
import ee.midaiganes.cache.SingleVmPoolUtil;
import ee.midaiganes.services.exceptions.DuplicateUsernameException;

@Resource(name = PortalConfig.USER_REPOSITORY)
public class UserRepository {
    private final SingleVmCache cache;
    private final UserDao userDao;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
        this.cache = SingleVmPoolUtil.getCache(UserRepository.class.getName());
    }

    public long getUsersCount() {
        return userDao.getUsersCount();
    }

    public List<User> getUsers(long start, long count) {
        return userDao.getUsers(start, count);
    }

    public User getUser(long userid) {
        String cacheKey = Long.toString(userid);
        User user = cache.get(cacheKey);
        if (user == null) {
            user = userDao.getUser(userid);
            if (user != null) {
                cache.put(cacheKey, user);
            }
        }
        return user;
    }

    public List<User> getUsers(long[] userIds) {
        if (userIds != null && userIds.length >= 0) {
            List<User> users = new ArrayList<>(userIds.length);
            List<Long> qryUserIds = new ArrayList<>(userIds.length);
            for (long userId : userIds) {
                User user = cache.get(Long.toString(userId));
                if (user != null) {
                    users.add(user);
                } else {
                    qryUserIds.add(Long.valueOf(userId));
                }
            }
            if (!qryUserIds.isEmpty()) {
                users.addAll(getAndCacheUsers(qryUserIds.toArray(new Long[qryUserIds.size()])));
            }
            return users;
        }
        return Collections.emptyList();
    }

    private List<User> getAndCacheUsers(Long[] userIds) {
        List<User> users = userDao.getUsers(userIds);
        for (User u : users) {
            cache.put(Long.toString(u.getId()), u);
        }
        return users;
    }

    public long addUser(@Nonnull final String username, @Nonnull final String password) throws DuplicateUsernameException {
        // TODO plain text password
        return userDao.addUser(username, password);
    }

    public User getUser(String username, String password) {
        // TODO plain text password
        String cacheKey = "getUser#" + username + "#" + password;
        Element el = cache.getElement(cacheKey);
        if (el != null) {
            return el.get();
        }
        User user = null;
        try {
            user = userDao.getUser(username, password);
            if (user != null) {
                cache.put(Long.toString(user.getId()), user);
            }
        } finally {
            cache.put(cacheKey, user);
        }
        return user;
    }

    public User getUser(String username) {
        // TODO cache
        User user = userDao.getUser(username);
        if (user != null) {
            cache.put(Long.toString(user.getId()), user);
        }
        return user;
    }
}