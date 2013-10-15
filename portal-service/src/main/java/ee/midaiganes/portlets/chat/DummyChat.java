package ee.midaiganes.portlets.chat;

import ee.midaiganes.model.User;
import ee.midaiganes.portlets.chat.Chat.AddUserToChatResponse.AddUserToChatResponseStatus;
import ee.midaiganes.portlets.chat.Chat.SendAndRemoveUserChatMessages.SendAndRemoveUserChatMessagesStatus;
import ee.midaiganes.portlets.chat.Chat.SetUsersPublicInChatResponse.SetUsersPublicInChatResponseStatus;

public class DummyChat implements Chat {
	@Override
	public AddUserToChatResponse addUserToChat(User user) {
		return new AddUserToChatResponse(AddUserToChatResponseStatus.CHAT_NOT_FOUND);
	}

	@Override
	public SendMessageToChat sendMessageToChat(User user, String msg) {
		return SendMessageToChat.CHAT_NOT_FOUND;
	}

	@Override
	public SendPrivateMessageToUserResponse sendPrivateMessageToUser(User from, User to, String msg) {
		return SendPrivateMessageToUserResponse.CHAT_NOT_FOUND;
	}

	@Override
	public SendAndRemoveUserChatMessages sendAndRemoveUserChatMessages(SendAndRemoveUserChatMessagesRequest request) {
		return new SendAndRemoveUserChatMessages(SendAndRemoveUserChatMessagesStatus.CHAT_NOT_FOUND);
	}

	@Override
	public SetUsersPriveInChatResponse setUsersPrivateInChat(User user1, User user2) {
		return SetUsersPriveInChatResponse.CHAT_NOT_FOUND;
	}

	@Override
	public SetUsersPublicInChatResponse setUserPublicInChat(User user1) {
		return new SetUsersPublicInChatResponse(SetUsersPublicInChatResponseStatus.CHAT_NOT_FOUND);
	}

	@Override
	public int getNumberOfUsers() {
		// TODO
		throw new IllegalStateException();
	}

	@Override
	public void destroy() {
		throw new IllegalStateException();
	}
}