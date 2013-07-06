package ee.midaiganes.portlets.chat;

import java.util.List;

public class ChatMessage {
	private final Command command;
	private final List<ChatUser> usersToSend;

	public ChatMessage(Command command, List<ChatUser> usersToSend) {
		this.command = command;
		this.usersToSend = usersToSend;
	}

	public Command getCommand() {
		return command;
	}

	public List<ChatUser> getUsersToSend() {
		return usersToSend;
	}
}