package ee.midaiganes.portlets.chat;

import ee.midaiganes.model.User;
import ee.midaiganes.util.TimeProviderUtil;

public class ChatUser {
    private final User user;
    private long lastActive;
    private static final long ONE_MINUTE_IN_MILLIS = 60 * 1000;

    public ChatUser(User user) {
        this.user = user;
        this.lastActive = TimeProviderUtil.currentTimeMillis();
    }

    public boolean isTimedOut(long currentTimeMillis) {
        return lastActive + ONE_MINUTE_IN_MILLIS < currentTimeMillis;
    }

    public void updateActiveTime(long currentTimeMillis) {
        this.lastActive = currentTimeMillis;
    }

    public User getUser() {
        return user;
    }
}
