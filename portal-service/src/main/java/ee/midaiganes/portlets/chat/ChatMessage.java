package ee.midaiganes.portlets.chat;

import java.util.List;

public class ChatMessage {
	private final Message message;
	private final List<ChatUser> usersToSend;

	public ChatMessage(long senderUserId, String message, List<ChatUser> usersToSend) {
		this.message = new Message(senderUserId, message);
		this.usersToSend = usersToSend;
	}

	public Message getMessage() {
		return message;
	}

	public List<ChatUser> getUsersToSend() {
		return usersToSend;
	}
}