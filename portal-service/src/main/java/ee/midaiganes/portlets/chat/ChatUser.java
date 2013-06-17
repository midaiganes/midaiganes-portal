package ee.midaiganes.portlets.chat;

public class ChatUser {
	private final long userId;
	private long lastActiveTimeInMillis;

	public ChatUser(long userId) {
		this.userId = userId;
		this.lastActiveTimeInMillis = System.currentTimeMillis();
	}

	public long getUserId() {
		return userId;
	}

	public long getLastActiveTimeInMillis() {
		return lastActiveTimeInMillis;
	}

	public void setLastActiveTimeInMillis(long lastActiveTimeInMillis) {
		this.lastActiveTimeInMillis = lastActiveTimeInMillis;
	}

	public static long getMinLastActiveTime() {
		return System.currentTimeMillis() - (1000 * 60 * 60);
	}
}