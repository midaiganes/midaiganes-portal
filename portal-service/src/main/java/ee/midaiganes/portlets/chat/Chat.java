package ee.midaiganes.portlets.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ee.midaiganes.services.UserRepository;

public class Chat {
	private final List<ChatUser> users = new ArrayList<>();
	private final List<ChatMessage> messages = new ArrayList<>();

	public synchronized boolean addUserToChat(long userId) {
		boolean userAdded = !isUserInChat(userId) && users.add(new ChatUser(userId));
		if (userAdded) {
			addCommand(new Join(UserRepository.getInstance().getUser(userId)));
		}
		return userAdded;
	}

	public synchronized boolean isUserInChat(long userId) {
		return findUser(userId) != null;
	}

	public synchronized long[] getChatUserIds() {
		long[] userIds = new long[users.size()];
		int i = 0;
		for (ChatUser u : users) {
			userIds[i++] = u.getUserId();
		}
		return userIds;
	}

	private ChatUser findUser(long userId) {
		for (ChatUser user : users) {
			if (user.getUserId() == userId) {
				return user;
			}
		}
		return null;
	}

	public synchronized void updateUserLastActiveTime(long userId) {
		ChatUser user = findUser(userId);
		if (user != null) {
			user.setLastActiveTimeInMillis(System.currentTimeMillis());
		}
	}

	public synchronized void removeTimedOutUsers(long minActiveTimeInMillis) {
		for (int i = 0; i < users.size();) {
			ChatUser user = users.get(i);
			if (user.getLastActiveTimeInMillis() < minActiveTimeInMillis) {
				users.remove(i);
				addCommand(new Quit(user.getUserId()));
				continue;
			}
			i++;
		}
	}

	public synchronized void addMessage(long senderUserId, String message) {
		addCommand(new Message(senderUserId, message));
	}

	private void addCommand(Command command) {
		messages.add(new ChatMessage(command, new ArrayList<>(users)));
	}

	public synchronized List<ChatMessage> getAndRemoveUserOrOldMessages(long userId) {
		List<ChatMessage> msgs = new ArrayList<>();
		Iterator<ChatMessage> i = messages.iterator();
		long minLastActiveTime = ChatUser.getMinLastActiveTime();
		while (i.hasNext()) {
			ChatMessage m = i.next();
			Iterator<ChatUser> cu = m.getUsersToSend().iterator();
			while (cu.hasNext()) {
				ChatUser u = cu.next();
				if (u.getUserId() == userId) {
					msgs.add(m);
					cu.remove();
					break;
				} else if (u.getLastActiveTimeInMillis() < minLastActiveTime) {
					cu.remove();
				}
			}
			if (m.getUsersToSend().isEmpty()) {
				i.remove();
			}
		}
		return msgs;
	}
}