package ee.midaiganes.portlets.chat;

import java.util.List;

import ee.midaiganes.model.User;
import ee.midaiganes.portlets.chat.Chat.AddUserToChatResponse;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessages;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessagesRequest;
import ee.midaiganes.portlets.chat.Chat.SendMessageToChat;

public class ChatService {
	private final Chats chats = new Chats();

	public List<ChatModel> getChats() {
		return chats.getChats();
	}

	public AddUserToChatResponse addUserToChat(User user, long chatId) {
		return chats.getChat(chatId).addUserToChat(user);
	}

	public SendMessageToChat sendMessageToChat(User user, long chatId, String msg) {
		return chats.getChat(chatId).sendMessageToChat(user, msg);
	}

	public void sendPrivateMessageToUser(User from, User to, long chatId, String msg) {
		chats.getChat(chatId).sendPrivateMessageToUser(from, to, msg);
	}

	public SendAndRemoveUserChatMessages sendAndRemoveUserChatMessages(long chatId, SendAndRemoveUserChatMessagesRequest call) {
		return chats.getChat(chatId).sendAndRemoveUserChatMessages(call);
	}

	public void setUsersPrivateInChat(User user1, User user2, long chatId) {
		chats.getChat(chatId).setUsersPrivateInChat(user1, user2);
	}

	public void setUsersPublicInChat(User user1, long chatId) {
		chats.getChat(chatId);// TODO
	}

	public void destroyAll() {
		chats.destroyAll();
	}
}
