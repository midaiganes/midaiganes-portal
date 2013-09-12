package ee.midaiganes.portlets.chat;

public class ChatModel {
	private final long id;
	private final String name;
	private final int users;

	public ChatModel(long id, String name, int users) {
		this.id = id;
		this.name = name;
		this.users = users;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getUsers() {
		return users;
	}
}
