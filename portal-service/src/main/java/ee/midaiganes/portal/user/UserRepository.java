package ee.midaiganes.portal.user;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import ee.midaiganes.services.PasswordEncryptor;
import ee.midaiganes.services.exceptions.DuplicateUsernameException;

public class UserRepository {
    private final UserDao userDao;
    private final PasswordEncryptor encryptor;
    private final LoadingCache<Long, User> cache;
    private final LoadingCache<UsernamePasswordCacheKey, Optional<User>> usernamePasswordCache;

    @Inject
    public UserRepository(UserDao userDao, PasswordEncryptor encryptor) {
        this.encryptor = encryptor;
        this.userDao = userDao;
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build(new CacheLoader<Long, User>() {
            @Override
            public User load(Long key) {
                User user = userDao.getUser(key.longValue());
                return Preconditions.checkNotNull(user);
            }
        });
        usernamePasswordCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).removalListener(new RemovalListener<UsernamePasswordCacheKey, Optional<User>>() {
            @Override
            public void onRemoval(RemovalNotification<UsernamePasswordCacheKey, Optional<User>> notification) {
                if (RemovalCause.EXPLICIT == notification.getCause()) {
                    Optional<User> value = notification.getValue();
                    User user = value != null ? value.orNull() : null;
                    if (user != null) {
                        cache.invalidate(Long.valueOf(user.getId()));
                    }
                }
            }
        }).build(new CacheLoader<UsernamePasswordCacheKey, Optional<User>>() {
            @Override
            public Optional<User> load(UsernamePasswordCacheKey key) throws Exception {
                User user = userDao.getUser(key.username, key.password);
                if (user != null) {
                    cache.put(Long.valueOf(user.getId()), user);
                }
                return Optional.fromNullable(user);
            }
        });
    }

    private static final class UsernamePasswordCacheKey {
        @Nonnull
        private final String username;
        @Nonnull
        private final String password;

        private UsernamePasswordCacheKey(String username, String password) {
            this.username = Preconditions.checkNotNull(username);
            this.password = Preconditions.checkNotNull(password);
        }

        @Override
        public int hashCode() {
            return password.hashCode() + username.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof UsernamePasswordCacheKey) {
                UsernamePasswordCacheKey o = (UsernamePasswordCacheKey) obj;
                return username.equals(o.username) && password.equals(o.password);
            }
            return false;
        }
    }

    public long getUsersCount() {
        return userDao.getUsersCount();
    }

    public List<User> getUsers(long start, long count) {
        return userDao.getUsers(start, count);
    }

    public User getUser(long userid) {
        return cache.getUnchecked(Long.valueOf(userid));
    }

    public long addUser(@Nonnull String username, @Nonnull String password) throws DuplicateUsernameException {
        String encryptedPassword = encryptor.encrypt(password);
        long userId = userDao.addUser(username, encryptedPassword);
        usernamePasswordCache.invalidate(new UsernamePasswordCacheKey(username, encryptedPassword));
        return userId;
    }

    @Nullable
    public User getUser(@Nonnull String username, @Nonnull String password) {
        String encryptedPassword = encryptor.encrypt(password);
        return usernamePasswordCache.getUnchecked(new UsernamePasswordCacheKey(username, encryptedPassword)).orNull();
    }

    public User getUser(String username) {
        // TODO cache
        User user = userDao.getUser(username);
        if (user != null) {
            cache.put(Long.valueOf(user.getId()), user);
        }
        return user;
    }
}