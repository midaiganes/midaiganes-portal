package ee.midaiganes.portal.user;

import java.io.Serializable;

import ee.midaiganes.model.PortalResource;
import ee.midaiganes.util.StringPool;

public class User implements Serializable, PortalResource {
    private static final long serialVersionUID = 1L;
    public static final long DEFAULT_USER_ID = 0;
    private static final User DEFAULT_USER = new User();

    private final long id;
    private final String username;

    private User() {
        this.id = DEFAULT_USER_ID;
        this.username = StringPool.EMPTY;
    }

    public User(long id, String username) {
        if (id == DEFAULT_USER_ID) {
            throw new IllegalArgumentException("Default user id not allowed");
        }
        if (username == null) {
            throw new IllegalArgumentException("Username is null");
        }
        this.id = id;
        this.username = username;
    }

    public static User getDefault() {
        return DEFAULT_USER;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isDefaultUser() {
        return isDefaultUserId(id);
    }

    public static boolean isDefaultUserId(long id) {
        return id == DEFAULT_USER_ID;
    }

    @Override
    public String getResource() {
        return User.class.getName();
    }

    @Override
    public int hashCode() {
        return ((int) id) + username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User u = (User) obj;
            return id == u.id && username.equals(u.username);
        }
        return false;
    }
}
