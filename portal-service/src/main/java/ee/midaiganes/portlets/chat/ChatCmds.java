package ee.midaiganes.portlets.chat;

import java.util.List;

public class ChatCmds {
	private final List<ChatCmd<?>> messages;

	public ChatCmds(List<ChatCmd<?>> messages) {
		this.messages = messages;
	}

	public List<ChatCmd<?>> getMessages() {
		return messages;
	}

	@Override
	public String toString() {
		return "ChatCmds [messages=" + messages + "]";
	}
}
