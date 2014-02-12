package ee.midaiganes.portlets.chat;

import java.util.List;

import ee.midaiganes.portal.user.User;

public interface Chat {
	public static class AddUserToChatResponse {
		enum AddUserToChatResponseStatus {
			CHAT_IS_FULL, CHAT_NOT_FOUND, USER_ALREADY_IN_CHAT, SUCCESS
		}

		private final AddUserToChatResponseStatus status;
		private final List<User> users;

		public AddUserToChatResponse(AddUserToChatResponseStatus status) {
			this(status, null);
		}

		public AddUserToChatResponse(AddUserToChatResponseStatus status, List<User> users) {
			this.status = status;
			this.users = users;
		}

		public AddUserToChatResponseStatus getStatus() {
			return status;
		}

		public List<User> getUsers() {
			return users;
		}
	}

	public static class SendAndRemoveUserChatMessagesRequest {
		private final User user;
		private final GetAsyncCallback callback;

		public static interface AsyncCallback {
			/** this should start new thread! **/
			void call(ChatCmds messages);

			void timeout();

			void userNotInChat();

			boolean isTimedOut(long currentTimeInMillis);
		}

		public static interface GetAsyncCallback {
			AsyncCallback getAsyncCallback();
		}

		public SendAndRemoveUserChatMessagesRequest(User user, GetAsyncCallback callback) {
			this.user = user;
			this.callback = callback;
		}

		public User getUser() {
			return user;
		}

		public GetAsyncCallback getCallback() {
			return callback;
		}
	}

	enum SendMessageToChat {
		CHAT_NOT_FOUND, USER_NOT_IN_CHAT, SUCCESS
	}

	enum SendPrivateMessageToUserResponse {
		CHAT_NOT_FOUND, USER_NOT_IN_CHAT, AT_LEAST_ONE_USER_IS_IN_PRIVATE_CHAT, SUCCESS
	}

	enum SetUsersPriveInChatResponse {
		CHAT_NOT_FOUND, USER_NOT_IN_CHAT, USER_ALREADY_IN_PRIVATE_CHAT, SUCCESS
	}

	public static class SetUsersPublicInChatResponse {
		enum SetUsersPublicInChatResponseStatus {
			CHAT_NOT_FOUND, USER_NOT_IN_PRIVATE_CHAT, SUCCESS
		}

		private final SetUsersPublicInChatResponseStatus status;
		private final List<User> userInChat;

		public SetUsersPublicInChatResponse(SetUsersPublicInChatResponseStatus status) {
			this(status, null);
		}

		public SetUsersPublicInChatResponse(SetUsersPublicInChatResponseStatus status, List<User> usersInChat) {
			this.status = status;
			this.userInChat = usersInChat;
		}

		public SetUsersPublicInChatResponseStatus getStatus() {
			return status;
		}

		public List<User> getUserInChat() {
			return userInChat;
		}
	}

	public static class SendAndRemoveUserChatMessages {
		enum SendAndRemoveUserChatMessagesStatus {
			CHAT_NOT_FOUND, USER_NOT_IN_CHAT, SUCCESS, SUCCESS_WAITING
		}

		private final SendAndRemoveUserChatMessagesStatus status;
		private final ChatCmds commands;

		public SendAndRemoveUserChatMessages(SendAndRemoveUserChatMessagesStatus status) {
			this(status, null);
		}

		public SendAndRemoveUserChatMessages(SendAndRemoveUserChatMessagesStatus status, ChatCmds commands) {
			this.status = status;
			this.commands = commands;
		}

		public SendAndRemoveUserChatMessagesStatus getStatus() {
			return status;
		}

		public ChatCmds getCommands() {
			return commands;
		}

		@Override
		public String toString() {
			return "SendAndRemoveUserChatMessages [status=" + status + ", commands=" + commands + "]";
		}
	}

	AddUserToChatResponse addUserToChat(User user);

	SendMessageToChat sendMessageToChat(User user, String msg);

	SendPrivateMessageToUserResponse sendPrivateMessageToUser(User from, User to, String msg);

	// TODO
	SendAndRemoveUserChatMessages sendAndRemoveUserChatMessages(SendAndRemoveUserChatMessagesRequest request);

	SetUsersPriveInChatResponse setUsersPrivateInChat(User currentUser, User user2);

	SetUsersPublicInChatResponse setUserPublicInChat(User currentUser);

	int getNumberOfUsers();

	void destroy();
}
