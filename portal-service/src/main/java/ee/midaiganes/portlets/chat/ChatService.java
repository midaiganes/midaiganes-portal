package ee.midaiganes.portlets.chat;

import java.util.List;

import ee.midaiganes.services.JobSchedulingService;

public class ChatService {
	private final JobSchedulingService jobSchedulingService;

	private final Chats chats = new Chats();
	private static final long[] EMPTY_LONG_ARRAY = new long[0];

	public ChatService() {
		jobSchedulingService = JobSchedulingService.getInstance();
		jobSchedulingService.runAtInterval(new UserRemoverJob(chats));
	}

	public boolean addUserToChat(long userId, String chatId) {
		return chats.getOrCreateChat(chatId).addUserToChat(userId);
	}

	public long[] getChatUserIds(String chatId) {
		Chat chat = chats.getChat(chatId);
		if (chat != null) {
			return chat.getChatUserIds();
		}
		return EMPTY_LONG_ARRAY;
	}

	public void updateUserLastActiveTime(long userId, String chatId) {
		Chat chat = chats.getChat(chatId);
		if (chat != null) {
			chat.updateUserLastActiveTime(userId);
		}
	}

	public boolean isUserInChat(long userId, String chatId) {
		Chat chat = chats.getChat(chatId);
		if (chat != null) {
			return chat.isUserInChat(userId);
		}
		return false;
	}

	public void addMessage(long senderUserId, String chatId, String message) {
		Chat chat = chats.getChat(chatId);
		if (chat != null) {
			chat.addMessage(senderUserId, message);
		}
	}

	public List<ChatMessage> getUserMessages(long userId, String chatId) {
		Chat chat = chats.getChat(chatId);
		if (chat != null) {
			return chat.getAndRemoveUserOrOldMessages(userId);
		}
		return null;
	}
}
