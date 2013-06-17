package ee.midaiganes.portlets.chat;

public class Message {
	private final long userId;
	private final String message;

	public Message(long userId, String message) {
		this.userId = userId;
		this.message = message;
	}

	public long getUserId() {
		return userId;
	}

	public String getMessage() {
		return message;
	}
}
