package ee.midaiganes.portlets.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Chat {
	private final List<ChatUser> users = new ArrayList<>();
	private final List<ChatMessage> messages = new ArrayList<>();

	public synchronized boolean addUserToChat(long userId) {
		return !isUserInChat(userId) && users.add(new ChatUser(userId));
	}

	public synchronized boolean isUserInChat(long userId) {
		return findUser(userId) != null;
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
			if (users.get(i).getLastActiveTimeInMillis() < minActiveTimeInMillis) {
				users.remove(i);
				continue;
			}
			i++;
		}
	}

	public synchronized void addMessage(long senderUserId, String message) {
		messages.add(new ChatMessage(senderUserId, message, new ArrayList<>(users)));
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