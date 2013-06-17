package ee.midaiganes.portlets.chat;

import java.util.HashMap;
import java.util.Map;

public class Chats {
	private final Map<String, Chat> chats = new HashMap<>();

	public synchronized Chat getOrCreateChat(String chatId) {
		Chat chat = chats.get(chatId);
		if (chat == null) {
			chat = new Chat();
			chats.put(chatId, chat);
		}
		return chat;
	}

	public synchronized Chat getChat(String chatId) {
		return chats.get(chatId);
	}

	public synchronized void removeTimedOutUsers(long minActiveTimeInMillis) {
		for (Chat chat : chats.values()) {
			chat.removeTimedOutUsers(minActiveTimeInMillis);
		}
	}
}