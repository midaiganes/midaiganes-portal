package ee.midaiganes.portlets.chat;

import java.util.List;

import ee.midaiganes.portal.user.User;

public class ChatCmd<A> {
	private final Cmd cmd;
	private final A obj;

	public ChatCmd(Cmd cmd, A obj) {
		this.cmd = cmd;
		this.obj = obj;
	}

	public Cmd getCmd() {
		return cmd;
	}

	public A getObj() {
		return obj;
	}

	@Override
	public String toString() {
		return "ChatCmd [cmd=" + cmd + ", obj=" + obj + "]";
	}

	enum Cmd {
		MSG, PRIVMSG, JOIN, USER_LEFT_PRIVATE
	}

	public static class MsgChatCmdData {
		private final String msg;
		private final User user;

		private MsgChatCmdData(String msg, User user) {
			this.msg = msg;
			this.user = user;
		}

		public String getMsg() {
			return msg;
		}

		public User getUser() {
			return user;
		}

		@Override
		public String toString() {
			return "MsgChatCmdData [msg=" + msg + ", user=" + user + "]";
		}
	}

	public static class MsgChatCmd extends ChatCmd<MsgChatCmdData> {
		public MsgChatCmd(String msg, User user) {
			super(Cmd.MSG, new MsgChatCmdData(msg, user));
		}
	}

	public static class JoinChatCmd extends ChatCmd<User> {
		public JoinChatCmd(User user) {
			super(Cmd.JOIN, user);
		}
	}

	public static class PrivMsgChatCmd extends ChatCmd<PrivMsgChatCmdData> {
		public PrivMsgChatCmd(String msg, long fromUserId) {
			super(Cmd.PRIVMSG, new PrivMsgChatCmdData(msg, fromUserId));
		}
	}

	public static class PrivMsgChatCmdData {
		private final String msg;
		private final long fromUserId;

		public PrivMsgChatCmdData(String msg, long fromUserId) {
			this.msg = msg;
			this.fromUserId = fromUserId;
		}

		public String getMsg() {
			return msg;
		}

		public long getFromUserId() {
			return fromUserId;
		}
	}

	public static class UserLeftPrivateChatCmd extends ChatCmd<List<User>> {
		public UserLeftPrivateChatCmd(List<User> users) {
			super(Cmd.USER_LEFT_PRIVATE, users);
		}
	}
}
